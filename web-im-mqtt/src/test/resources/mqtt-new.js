// 适用于njs-0.2.4之后版本
var client_messages = 1;
var clientKey = "";
function getClientId(s) {
	 s.on('upload', function (data) {
	        if (data == '') {
	            return;
	        }

	        var packet_type_flags_byte = data.codePointAt(0);
	        // First MQTT packet contain message type and flags. CONNECT message type
	        // is encoded as 0001, and we're not interested in flags, so only values
	        // 0001xxxx (which is between 16 and 32) should be checked.
	        if (packet_type_flags_byte < 16 || packet_type_flags_byte >= 32) {
	            s.off('upload');
	            s.allow();
	            return;
	        }

	        if (clientKey === '') {
	            clientKey = parseClientId(s,data);
	        }
	        	        
	        s.off('upload');
	        s.allow();
	 })      
}	        

function parsePwd(s, data) {
    // An explanation of MQTT packet structure can be found here:
    // https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#msg-format. 

    // CONNECT message is explained here:
    // https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connect.

    /*
        0               1               2               3
        7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0 7 6 5 4 3 2 1 0
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        | TYPE | RSRVD | REMAINING LEN |      PROTOCOL NAME LEN       |
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        |                        PROTOCOL NAME                        |    
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-|
        |    VERSION   |     FLAGS     |          KEEP ALIVE          | 
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-|
        |                     Payload (if any) ...                    |
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        First byte with remaining length represents fixed header.
        Remaining Length is the length of the variable header (10 bytes) plus the length of the Payload.
        It is encoded in the manner described here:
        http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/errata01/os/mqtt-v3.1.1-errata01-os-complete.html#_Toc442180836.
        
        Connect flags byte looks like this:   
        |       7       |       6       |       5     |   4  3   |     2     |       1       |     0     |
        | Username Flag | Password Flag | Will Retain | Will QoS | Will Flag | Clean Session | Reserved  |
        The payload is determined by the flags and comes in this order:
            1. Client ID (2 bytes length + ID value)
            2. Will Topic (2 bytes length + Will Topic value) if Will Flag is 1.
            3. Will Message (2 bytes length + Will Message value) if Will Flag is 1.
            4. User Name (2 bytes length + User Name value) if User Name Flag is 1.
            5. Password (2 bytes length + Password value) if Password Flag is 1.
        
        This method extracts Password field.
    */

    // Extract variable length header. It's 1-4 bytes. As long as continuation byte is
    // 1, there are more bytes in this header. This algorithm is explained here:
    // http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/errata01/os/mqtt-v3.1.1-errata01-os-complete.html#_Toc442180836
    var len_size = 1;
    for (var remaining_len = 1; remaining_len < 5; remaining_len++) {
        if (data.codePointAt(remaining_len) > 128) {
            len_size += 1;
            continue;
        }
        break;
    }

    // CONTROL(1) + MSG_LEN(1-4) + PROTO_NAME_LEN(2) + PROTO_NAME(4) + PROTO_VERSION(1)
    var flags_pos = 1 + len_size + 2 + 4 + 1;
    var flags = data.codePointAt(flags_pos);
    
    // If there are no username and password flags (11xxxxxx), return.
    if (flags < 192) {
        s.error('MQTT username or password not provided');
        return '';
    }
    
    // FLAGS(1) + KEEP_ALIVE(2)
    var shift = flags_pos + 1 + 2;
    
    // Number of bytes to encode length.
    var len_bytes_num = 2;

    // If Wil Flag is present, Will Topic and Will Message need to be skipped as well.
    var shift_flags = 196 <= flags ? 5 : 3;
    var len_msb, len_lsb, len;
    
    for (var i = 0; i < shift_flags; i++) {
        len_msb = data.codePointAt(shift).toString(16);
        len_lsb = data.codePointAt(shift + 1).toString(16);
        len = calcLen(len_msb, len_lsb);
        shift += len_bytes_num;
        if (i != shift_flags - 1) {
            shift += len;
        }
    }

    var password = data.substring(shift, shift + len);
    return password;
}

function calcLen(msb, lsb) {
    if (lsb < 2) {
        lsb = '0' + lsb;
    }

    return parseInt(msb + lsb, 16);
}

function parseClientId(s,data) {
	if ( client_messages == 1 ) { // CONNECT is first packet from the client
            // CONNECT packet is 1, using upper 4 bits (00010000 to 00011111)
            var packet_type_flags_byte = data.charCodeAt(0);
            s.log("MQTT packet type+flags = " + packet_type_flags_byte.toString());
            if ( packet_type_flags_byte >= 16 && packet_type_flags_byte < 32 ) {
                // Calculate remaining length with variable encoding scheme
                var multiplier = 1;
                var remaining_len_val = 0;
                var remaining_len_byte;
                for (var remaining_len_pos = 1; remaining_len_pos < 5; remaining_len_pos++ ) {
                    remaining_len_byte = data.charCodeAt(remaining_len_pos);
                    if ( remaining_len_byte == 0 ) break; // Stop decoding on 0
                    remaining_len_val += (remaining_len_byte & 127) * multiplier;
                    multiplier *= 128;
                }
                // Extract ClientId based on length defined by 2-byte encoding
                var payload_offset = remaining_len_pos + 12; // Skip fixed header
                var client_id_len_msb = data.charCodeAt(payload_offset).toString(16);
                var client_id_len_lsb = data.charCodeAt(payload_offset + 1).toString(16);
                if ( client_id_len_lsb.length < 2 ) client_id_len_lsb = "0" + client_id_len_lsb;
                var client_id_len_int = parseInt(client_id_len_msb + client_id_len_lsb, 16);
                clientKey = data.substr(payload_offset + 2, client_id_len_int);
                s.log("ClientId value  = " + clientKey);
            } else {
                s.log("Received unexpected MQTT packet type+flags: " + packet_type_flags_byte.toString());
            }
    }
    client_messages++;
    return clientKey;
}

function setClientId(s) {
    return clientKey;
}


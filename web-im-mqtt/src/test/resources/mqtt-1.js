var first_message = true;
var client_properties = {
  clientId: "-",
  username: "",
  password: ""
}

var devices = {}
devices["device1"] = "token1";
devices["device2"] = "token2";

function replaceTopic(s) {
  if (!s.fromUpstream) {
    if (isMQTTPublishPackage(s)) {
      if (s.buffer.indexOf("iot-2/type") != -1) {
        s.buffer = s.buffer.replace("iot-2/type", "iot-2-type").toBytes();
      }
    }
  }
}

function getClientId(s) {
  if (!s.fromUpstream) {

    if (s.buffer.toString().length == 0) { // Initial calls may
      s.log("No buffer yet"); // contain no data, so
      return s.AGAIN; // ask that we get called again
    }
    else if (first_message && isMQTTConnectPackage(s)) { // CONNECT is first packet from the client
      first_message = false;
      // Calculate remaining length with variable encoding scheme
      var multiplier = 1;
      var remaining_len_val = 0;
      var remaining_len_byte;
      for (var remaining_len_pos = 1; remaining_len_pos < 5; remaining_len_pos++) {
        remaining_len_byte = s.buffer.charCodeAt(remaining_len_pos);
        if (remaining_len_byte == 0) break; // Stop decoding on 0
        remaining_len_val += (remaining_len_byte & 127) * multiplier;
        multiplier *= 128;
      }

      // Skip fixed header
      var payload_offset = remaining_len_pos + 12;
      // extract ClientId based on length defined by 2-byte encoding
      payload_offset += parseField(s, payload_offset, "clientId")
      // assume no LWT otherwise we need to parse the length and skip those fields
      // extract username
      payload_offset += parseField(s, payload_offset, "username")
      // extract password
      payload_offset += parseField(s, payload_offset, "password")

      if (!isAuthenticated()) {
        return s.ERROR;
      }
    } else {
      //s.log("mmm: not CONNECT package");
    }
  }
  return s.OK;
}

function isAuthenticated() {
  return devices[client_properties["username"]] == client_properties["password"]
}

function isMQTTConnectPackage(s) {
  // package type is 1, using upper 4 bits (00010000 to 00011111)
  var package_type = s.buffer.charCodeAt(0);
  if (package_type >= 16 && package_type < 32) {
    return true;
  } else {
    s.log("No MQTT Connect package - invalid packet type: " + package_type.toString());
    return false;
  }
}

function isMQTTPublishPackage(s) {
  // (00110000-00111111)
  var package_type = s.buffer.charCodeAt(0);
  if (package_type >= 48 && package_type < 64) {
    return true;
  } else {
    s.log("No MQTT Connect package - invalid packet type: " + package_type.toString());
    return false;
  }
}

function parseField(s, offset, property_name) {
  var len_int = parseLengthOfField(s, offset);
  client_properties[property_name] = s.buffer.substr(offset + 2, len_int);
  return 2 + len_int;
}

function parseLengthOfField(s, offset) {
  var len_msb = s.buffer.charCodeAt(offset).toString(16);
  var len_lsb = s.buffer.charCodeAt(offset + 1).toString(16);
  if (len_lsb.length < 2) len_lsb = "0" + len_lsb;
  return parseInt(len_msb + len_lsb, 16);
}

function setClientId(s) {
  return client_properties["clientId"];
}
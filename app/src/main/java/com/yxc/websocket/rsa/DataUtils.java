package com.yxc.websocket.rsa;


import org.json.JSONObject;

public final class DataUtils {
	public static final String TAG="DataUtils";
	private static final String DEFAULT_PUBLIC_KEY = "your  first pair public key";
	private static final String DEFAULT_PRIVATE_KEY = "your second pair private key";
	
	public static String encodeRequest(JSONObject json) throws Exception{
		byte[] data = json.toString().getBytes();
		byte[] encodedData = RSAUtils.encryptByPublicKey(data, DEFAULT_PUBLIC_KEY);

		return Base64Utils.encode(encodedData);
		
	}
	
	
	public static String decodeAppResponse(String response) throws Exception{
		byte[] responseData = Base64Utils.decode(response);
		byte[] decodedData = RSAUtils.decryptByPrivateKey(responseData,
				DEFAULT_PRIVATE_KEY);
		String responseText = new String(decodedData);
		return responseText;
	}
	
	
	
	
	
	
	
	
	
	
	
}

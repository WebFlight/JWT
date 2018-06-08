package jwt.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;

public class RSAKeyPairReader {
	
	public RSAPublicKey getPublicKey(IContext context, jwt.proxies.PublicKey publicKeyObject) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream inputStream = Core.getFileDocumentContent(context, publicKeyObject.getMendixObject());
		byte[] encodedPublicKey = new byte[inputStream.available()];
		inputStream.read(encodedPublicKey);
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		
		return (RSAPublicKey) publicKey;
	}
	
	public RSAPrivateKey getPrivateKey(IContext context, jwt.proxies.PrivateKey privateKeyObject) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream inputStream = Core.getFileDocumentContent(context, privateKeyObject.getMendixObject());
		byte[] encodedPrivateKey = new byte[inputStream.available()];
		inputStream.read(encodedPrivateKey);
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		
		return (RSAPrivateKey) privateKey;
	}
}

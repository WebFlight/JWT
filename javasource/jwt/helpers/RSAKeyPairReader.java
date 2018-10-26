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
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;

import jwt.proxies.JWTRSAPrivateKey;
import jwt.proxies.JWTRSAPublicKey;

public class RSAKeyPairReader {
	
	public RSAPublicKey getPublicKey(IContext context, JWTRSAPublicKey publicKeyObject) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream inputStream = Core.getFileDocumentContent(context, publicKeyObject.getMendixObject());
		byte[] encodedPublicKey = new byte[inputStream.available()];
		inputStream.read(encodedPublicKey);
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		
		return (RSAPublicKey) publicKey;
	}
	
	public RSAPrivateKey getPrivateKey(IContext context, JWTRSAPrivateKey privateKeyObject) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream inputStream = Core.getFileDocumentContent(context, privateKeyObject.getMendixObject());
		byte[] encodedPrivateKey = new byte[inputStream.available()];
		inputStream.read(encodedPrivateKey);
		
		PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(encodedPrivateKey);
		
		JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
		PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
		
		return (RSAPrivateKey) privateKey;
	}
}

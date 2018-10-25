package jwt.helpers;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;

import jwt.proxies.JWTRSAKeyPair;
import jwt.proxies.JWTRSAPrivateKey;
import jwt.proxies.JWTRSAPublicKey;

public class RSAKeyPairGenerator {
	
	public JWTRSAKeyPair generate(IContext context, int keySize) throws NoSuchAlgorithmException, CoreException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);
		KeyPair keyPair = keyPairGenerator.genKeyPair(); 
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		JWTRSAKeyPair keyPairObject = new JWTRSAKeyPair(context);
		
		JWTRSAPublicKey publicKeyObject = new JWTRSAPublicKey(context);
		publicKeyObject.setJWTRSAPublicKey_JWTRSAKeyPair(context, keyPairObject);
		Core.commit(context, publicKeyObject.getMendixObject());
		
		JWTRSAPrivateKey privateKeyObject = new JWTRSAPrivateKey(context);
		privateKeyObject.setJWTRSAPrivateKey_JWTRSAKeyPair(context, keyPairObject);
		Core.commit(context, privateKeyObject.getMendixObject());
		
		Core.commit(context, keyPairObject.getMendixObject());
		
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		
		Core.storeFileDocumentContent(context, publicKeyObject.getMendixObject(), "public" + keyPairObject.getKeyPairId(context) + ".key", new ByteArrayInputStream(x509EncodedKeySpec.getEncoded()));
		Core.storeFileDocumentContent(context, privateKeyObject.getMendixObject(), "private" + keyPairObject.getKeyPairId(context) + ".key", new ByteArrayInputStream(pkcs8EncodedKeySpec.getEncoded()));
		
		return keyPairObject;	
	}
}
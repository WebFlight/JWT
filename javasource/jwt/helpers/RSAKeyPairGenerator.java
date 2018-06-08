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

public class RSAKeyPairGenerator {
	
	public jwt.proxies.KeyPair generate(IContext context, int keySize) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keySize);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			
			jwt.proxies.KeyPair keyPairObject = new jwt.proxies.KeyPair(context);
			
			jwt.proxies.PublicKey publicKeyObject = new jwt.proxies.PublicKey(context);
			publicKeyObject.setPublicKey_KeyPair(context, keyPairObject);
			Core.commit(context, publicKeyObject.getMendixObject());
			
			jwt.proxies.PrivateKey privateKeyObject = new jwt.proxies.PrivateKey(context);
			privateKeyObject.setPrivateKey_KeyPair(context, keyPairObject);
			Core.commit(context, privateKeyObject.getMendixObject());
			
			Core.commit(context, keyPairObject.getMendixObject());
			
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					publicKey.getEncoded());
			
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					privateKey.getEncoded());
			
			Core.storeFileDocumentContent(context, publicKeyObject.getMendixObject(), "public" + keyPairObject.getKeyPairId(context) + ".key", new ByteArrayInputStream(x509EncodedKeySpec.getEncoded()));
			Core.storeFileDocumentContent(context, privateKeyObject.getMendixObject(), "private" + keyPairObject.getKeyPairId(context) + ".key", new ByteArrayInputStream(pkcs8EncodedKeySpec.getEncoded()));
			
			return keyPairObject;
		} catch (NoSuchAlgorithmException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
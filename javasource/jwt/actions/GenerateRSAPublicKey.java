// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package jwt.actions;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import jwt.helpers.RSAKeyPairGenerator;
import jwt.helpers.RSAKeyPairReader;
import jwt.proxies.JWTRSAPublicKey;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Use this action to instantiate a public key in case your key pair has been generated by a third party.
 */
public class GenerateRSAPublicKey extends CustomJavaAction<IMendixObject>
{
	private java.lang.String modulus;
	private java.lang.String publicExponent;
	private java.lang.String issuer;
	private java.lang.String subject;
	private java.lang.Long yearsValidity;
	private IMendixObject __privateKey;
	private jwt.proxies.JWTRSAPrivateKey privateKey;

	public GenerateRSAPublicKey(IContext context, java.lang.String modulus, java.lang.String publicExponent, java.lang.String issuer, java.lang.String subject, java.lang.Long yearsValidity, IMendixObject privateKey)
	{
		super(context);
		this.modulus = modulus;
		this.publicExponent = publicExponent;
		this.issuer = issuer;
		this.subject = subject;
		this.yearsValidity = yearsValidity;
		this.__privateKey = privateKey;
	}

	@Override
	public IMendixObject executeAction() throws Exception
	{
		this.privateKey = __privateKey == null ? null : jwt.proxies.JWTRSAPrivateKey.initialize(getContext(), __privateKey);

		// BEGIN USER CODE
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
		
		RSAKeyPairReader rsaKeyPairReader = new RSAKeyPairReader();
		RSAPrivateKey rsaPrivateKey = rsaKeyPairReader.getPrivateKey(this.getContext(), privateKey);
		
		RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
		JWTRSAPublicKey publicKey = rsaKeyPairGenerator.generatePublic(this.getContext(), "public.der", rsaPublicKey, rsaPrivateKey, issuer, subject, Math.toIntExact(yearsValidity));
		
		return publicKey.getMendixObject();
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "GenerateRSAPublicKey";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}

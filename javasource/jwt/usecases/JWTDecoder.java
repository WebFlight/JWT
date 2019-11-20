package jwt.usecases;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.DataValidationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import jwt.helpers.AlgorithmParser;
import jwt.helpers.AudienceListToStringArrayConverter;
import jwt.helpers.DecodedJWTParser;
import jwt.helpers.RSAKeyPairReader;
import jwt.proxies.ENU_Algorithm;
import jwt.proxies.constants.Constants;

public class JWTDecoder {
	
	private ILogNode logger;
	private IContext context;
	private String token;
	
	public JWTDecoder(IContext context, String token) {
		this.logger = Core.getLogger(Constants.getLOGNODE());
		this.context = context;
		this.token = token;
	}
	
	public IMendixObject decodeToObject() {
		validateToken();
		return getDecodedJWTObject();
	}
	
	public IMendixObject verifyAndDecodeToObject(String secret, ENU_Algorithm algorithm, jwt.proxies.JWT claimsToVerify, jwt.proxies.JWTRSAPublicKey publicKey, Long leeway) {
		validateToken();
		validateAlgorithm(algorithm);
		verify(secret, algorithm, claimsToVerify, publicKey, leeway);
		return getDecodedJWTObject();
	}
	
	private void validateToken() {
		if (this.token == null || this.token.equals("")) {
			logger.error("Cannot decode an empty token.");
			throw new DataValidationRuntimeException("Cannot decode an empty token.");
		}
	}
	
	private void validateAlgorithm(ENU_Algorithm algorithm) {
		if (algorithm == null) {
			logger.error("Cannot decode token using an empty algorithm.");
			throw new DataValidationRuntimeException("Cannot decode token using an empty algorithm.");
		}
	}
	
	private void verify(String secret, ENU_Algorithm algorithm, jwt.proxies.JWT claimsToVerify, jwt.proxies.JWTRSAPublicKey publicKey, Long unvalidatedLeeway) {
		Long leeway = validateLeeway(unvalidatedLeeway);
		
		RSAPublicKey rsaPublicKey = null;
		
		if(publicKey != null) {
			RSAKeyPairReader rsaKeyPairReader = new RSAKeyPairReader();
			try {
				rsaPublicKey = rsaKeyPairReader.getPublicKey(this.context, publicKey);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Could not determine algorithm for public key.", e);
			} catch (InvalidKeySpecException e) {
				throw new RuntimeException("Could not determine public key specification.", e);
			} catch (IOException e) {
				throw new RuntimeException("Could not read public key.", e);
			}
		}
		
		try {
			Algorithm alg = new AlgorithmParser().parseAlgorithm(algorithm, secret, rsaPublicKey, null);
			logger.debug("Starting to decode JWT token with algorithm " + alg.getName() + ".");
			
			Verification verification = JWT.require(alg).acceptLeeway(leeway);
			
			if (claimsToVerify != null) {
				if (claimsToVerify.getiss() != null) {
					logger.debug("Verify issuer with value: " + claimsToVerify.getiss() + ".");
					verification.withIssuer(claimsToVerify.getiss());
				}
			
				if (claimsToVerify.getjti() != null) {
					logger.debug("Verify JWT token ID with value: " + claimsToVerify.getjti() + ".");
					verification.withJWTId(claimsToVerify.getjti());
				}
				
				if (claimsToVerify.getsub() != null) {
					logger.debug("Verify subject with value: " + claimsToVerify.getsub() + ".");
					verification.withSubject(claimsToVerify.getsub());
				}
				
				String[] audienceList = new AudienceListToStringArrayConverter().convert(this.context, claimsToVerify);
				
				if (audienceList.length > 0) {
					logger.debug("Verify with list of " + audienceList.length + " audiences.");
					verification.withAudience(audienceList);
				}
			}
			
			JWTVerifier verifier = verification.build();
			verifier.verify(token);
			
			logger.debug("Verifying token successfull.");
		} catch (UnsupportedEncodingException exception){
		    logger.error("Token encoding unsupported.", exception);
		    throw new RuntimeException(exception);
		} catch (JWTVerificationException exception){
			logger.warn("Verification of token signature/claims failed: " + exception.getMessage());
			throw exception;
		} 
		
	}
	
	private Long validateLeeway(Long leeway) {
		if(leeway == null || leeway < 0) {
			return 0L;
		}
		
		return leeway;
	}
	
	private IMendixObject getDecodedJWTObject() {
		DecodedJWT jwt = JWT.decode(token);
		IMendixObject jwtObject =  new DecodedJWTParser()
		.parse(this.context, logger, jwt)
		.getMendixObject();
		
		return jwtObject;
	}

}

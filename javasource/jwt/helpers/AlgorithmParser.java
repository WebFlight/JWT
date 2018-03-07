package jwt.helpers;

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.algorithms.Algorithm;

import jwt.proxies.ENU_Algorithm;

public class AlgorithmParser {

	public Algorithm parseAlgorithm(ENU_Algorithm algorithm, String secret) throws IllegalArgumentException, UnsupportedEncodingException {
		
		switch(algorithm) {
			case HS256:
				return Algorithm.HMAC256(secret);
			case HS384:
				return Algorithm.HMAC384(secret);
			case HS512:
				return Algorithm.HMAC512(secret);
			default:
				return Algorithm.HMAC256(secret);
		}
	}
	
}

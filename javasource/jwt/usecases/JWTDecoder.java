package jwt.usecases;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.DataValidationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import jwt.helpers.DecodedJWTParser;
import jwt.helpers.OutputType;
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
	
	public IMendixObject decode(boolean verifyToken, OutputType outputType) {
		validateInput();
		
		if (outputType.equals(OutputType.JWT_OBJECT)) {
			return getDecodedJWTObject();
		}
		
		return null;
	}
	
	private void validateInput() {
		if (this.token == null || this.token.equals("")) {
			logger.error("Cannot decode an empty token.");
			throw new DataValidationRuntimeException("Cannot decode an empty token.");
		}
	}
	
	private IMendixObject getDecodedJWTObject() {
		DecodedJWT jwt = JWT.decode(token);
		IMendixObject jwtObject =  new DecodedJWTParser()
		.parse(this.context, logger, jwt)
		.getMendixObject();
		
		return jwtObject;
	}

}

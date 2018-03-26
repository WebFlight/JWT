package jwt.helpers;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;

import jwt.proxies.Audience;
import jwt.proxies.JWT;
import jwt.proxies.PublicClaimBoolean;
import jwt.proxies.PublicClaimDate;
import jwt.proxies.PublicClaimDecimal;
import jwt.proxies.PublicClaimInteger;
import jwt.proxies.PublicClaimLong;
import jwt.proxies.PublicClaimString;

public class DecodedJWTParser {
	
	public JWT parse(IContext context, ILogNode logger, DecodedJWT decodedJWT) {
		logger.debug("Started parsing of decoded JWT.");
		
		JWT jwt = new JWT(context);
		
		List<String> audiences = decodedJWT.getAudience();
		
		if (audiences != null) {
			for(String audienceString : audiences) {
				Audience audience = new Audience(context);
				logger.debug("Adding audience " + audienceString + ".");
				audience.setaud(audienceString);
				audience.setAudience_JWT(jwt);
			}
		}
		
		logger.debug("Setting other registered claims.");
		jwt.setiss(decodedJWT.getIssuer());
		jwt.setnbf(decodedJWT.getNotBefore());
		jwt.setsub(decodedJWT.getSubject());
		jwt.setiat(decodedJWT.getIssuedAt());
		jwt.setexp(decodedJWT.getExpiresAt());
		jwt.setjti(decodedJWT.getId());
		
		Map<String, Claim> claimMap = decodedJWT.getClaims();
		Set<Entry<String, Claim>> claimEntrySet = claimMap.entrySet();
		Iterator<Entry<String,Claim>> claimIterator = claimEntrySet.iterator();
		
		while (claimIterator.hasNext()) {
			Entry<String,Claim> claimEntry = claimIterator.next();
			String value = claimEntry.getKey();
			Claim claim = claimEntry.getValue();
			
			RegisteredClaimIdentifier registeredClaimIdentifier = new RegisteredClaimIdentifier();
			
			// Skip registered claims. These are included in the JWT object and associated audience objects.
			if (registeredClaimIdentifier.identify(value)) {
				logger.debug("Skip parsing claim: " + value + ", because registered claim is already included in JWT object.");
				continue;
			}
			
			if (claim.asString() != null) {
				logger.debug("Parse claim " + value + " as String claim.");
				PublicClaimString publicClaimString = new PublicClaimString(context);
				publicClaimString.setClaim_JWT(jwt);
				publicClaimString.setClaim(value);
				publicClaimString.setValue(claim.asString());
				continue;
			}
			
			if (claim.asBoolean() != null) {
				logger.debug("Parse claim " + value + " as Boolean claim.");
				PublicClaimBoolean publicClaimBoolean = new PublicClaimBoolean(context);
				publicClaimBoolean.setClaim_JWT(jwt);
				publicClaimBoolean.setClaim(value);
				publicClaimBoolean.setValue(claim.asBoolean());
				continue;
			}
			
			if (claim.asInt() != null) {
				if (claim.asInt().doubleValue() == claim.asDouble()) {
					logger.debug("Parse claim " + value + " as Integer claim.");
					PublicClaimInteger publicClaimInteger = new PublicClaimInteger(context);
					publicClaimInteger.setClaim_JWT(jwt);
					publicClaimInteger.setClaim(value);
					publicClaimInteger.setValue(claim.asInt());
					continue;
				}
			}
			
			if (claim.asLong() != null) {
				if (claim.asLong().doubleValue() == claim.asDouble()) {
					logger.debug("Parse claim " + value + " as Long claim.");
					PublicClaimLong publicClaimLong = new PublicClaimLong(context);
					publicClaimLong.setClaim_JWT(jwt);
					publicClaimLong.setClaim(value);
					publicClaimLong.setValue(claim.asLong());
					continue;
				}
			}
			
			if (claim.asDouble() != null) {
				logger.debug("Parse claim " + value + " as Decimal claim.");
				PublicClaimDecimal publicClaimDecimal = new PublicClaimDecimal(context);
				publicClaimDecimal.setClaim_JWT(jwt);
				publicClaimDecimal.setClaim(value);
				publicClaimDecimal.setValue(BigDecimal.valueOf(claim.asDouble()));
				continue;
			}
			
			if (claim.asDate() != null) {
				logger.debug("Parse claim " + value + " as Date claim.");
				PublicClaimDate publicClaimDate = new PublicClaimDate(context);
				publicClaimDate.setClaim_JWT(jwt);
				publicClaimDate.setClaim(value);
				publicClaimDate.setValue(claim.asDate());
				continue;
			}
				
			// Claim has a format that is not yet supported, e.g. an array.
			logger.warn("Could not parse Claim " + value + " while decoding token. Format is not supported.");
			
		}
		
		logger.debug("Parsing token completed.");
		
		return jwt;
	}

}

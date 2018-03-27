# Mendix JWT module

Welcome to the Mendix JWT (JSON Web Token) module. This module can be used in [Mendix](http://www.mendix.com) apps to generate and decode JWT tokens. The app uses the com.auth0/java-jwt/3.3.0 library. JSON Web Tokens are often used to perform token authentication in web services. Try it at [JWT.io](https://jwt.io)!

## Related resources
* auth0/java-jwt on [GitHub](https://github.com/auth0/java-jwt/)
* [RFC 7519](https://tools.ietf.org/html/rfc7519) JSON Web Token (JWT) specs

# Table of Contents

* [Getting Started](#getting-started)
* [Application](#application)
	- [Supported](#supported)
	- [Not supported](#not-supported)
	- [Logging](#logging)
	- [Dependencies](#dependencies)
* [Development Notes](#development-notes)

# Getting started
1. The *JWT* module can be downloaded from within the Mendix Business Modeler in the Mendix Appstore into any model that is build with Mendix 7.12.0+.
2. Apply the Java actions in the _USE_ME folder or use the *Generate JWT* and *Decode JWT* activities in the Toolbox in the Integration activities category.

# Application
Once the JWT module is imported in your Mendix model, the Java actions can be used in microflows.

## Supported
* Algorithms
	- HMAC with SHA-256
	- HMAC with SHA-384
	- HMAC with SHA-512
* Registered claims according to [RFC 7519](https://tools.ietf.org/html/rfc7519)
	- Encoding all registered claims (including array of audiences)
	- Decoding all registered claims (including array of audiences)
	- Verify registered claims jti (JWT ID), sub (subject), aud (audience) and iss (issuer). The *Decode JWT* throws an exception when the token is not valid or could not be verified. Be sure to catch the exceptions in the microflow if additional logic has be executed.
	- Check for expiry dates when decoding (exp claim), which is automatically done by the underlying JWT library
* Public claims
	- Encode public claims with different types:
		- Boolean
		- DateTime
		- Integer
		- Long
		- Decimal
		- String
	- Decode public claims with different types:
		- Boolean
		- Integer (DateTime will be returned as Integer, because UNIX time stamp is used in JWT)
		- Long
		- Decimal
		- String

## Not supported
* Algorithms
	- RSA 
	- ECDSA
* Encoding public claims containing an array of values
* Decoding public claims containing an array of values
* Have access to values in the JWT Header (the JWT header is automatically verified by the underlying library)

## Logging
* The JWT Log node is available for more information.

## Dependencies
The JWT module implements the auth0/java-jwt/3.3.0 library, which has the following dependencies that are included in the module package:
* com.fasterxml.jackson.core/jackson-databind/2.9.2
	- com.fasterxml.jackson.core/jackson-annotations/2.9.0
	- com.fasterxml.jackson.core/jackson-core/2.9.2
* commons-codec/commons-codec/1.11

# Development notes
* Functionality is tested using the Mendix UnitTesting module. The tests are included in the JWTTest module.

 [1]: docs/logo.png

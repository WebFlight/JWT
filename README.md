# Mendix JWT module

Welcome to the Mendix JWT (JSON Web Token) module. This module can be used in [Mendix](http://www.mendix.com) apps to generate and decode JWT tokens. The app uses the com.auth0/java-jwt/3.8.0 library. JSON Web Tokens are often used to perform token authentication in web services. Try it at [JWT.io](https://jwt.io)!

Contributed to the community by Ciphix - Webflight has been acquired by Ciphix per 2024!
![JWT logo][1]

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
1. The *JWT* module can be downloaded from within the Mendix Business Modeler in the Mendix Appstore into any model that is build with Mendix 7.13.1+.
2. Apply the Java actions in the _USE_ME folder or use the *Generate JWT* and *Decode JWT* activities in the Toolbox in the Integration activities category. Check the Examples folder to see how the Java actions can be used.

# Application
Once the JWT module is imported in your Mendix model, the Java actions can be used in microflows.
When using RSA algorithms, use the following OpenSSL commands to verify your self-generated or existing public and private keys:
```
openssl rsa -in private.der -inform DER
openssl x509 -in public.der -inform DER
```

## Supported
* Algorithms
	- HMAC with SHA-256
	- HMAC with SHA-384
	- HMAC with SHA-512
	- RSASSA-PKCS1-v1_5 with SHA-256
	- RSASSA-PKCS1-v1_5 with SHA-384
	- RSASSA-PKCS1-v1_5 with SHA-512
* Registered claims according to [RFC 7519](https://tools.ietf.org/html/rfc7519)
	- Encoding all registered claims (including array of audiences)
	- Decoding all registered claims (including array of audiences)
	- Verify registered claims jti (JWT ID), sub (subject), aud (audience) and iss (issuer). The *Decode JWT* throws an exception when the token is not valid or could not be verified. Be sure to catch the exceptions in the microflow if additional logic has be executed.
	- Check for expiry dates when decoding (exp claim), which is automatically done by the underlying JWT library. Specify leeway parameter in seconds to allow for more flexibiliy in time-based validations.
* Public claims
	- Encode and decode public claims with different types. When decoding public claims, all possible types are parsed in an object of the PublicClaimResponse entity. Type casts that are not possible by default (e.g. String to Decimal), will be left empty.
		- Boolean
		- DateTime
		- Integer
		- Long
		- Decimal
		- String
	- When using the *Decode & Verify JWT Plain Text*, you will have access to the plain text header and payload JSON. Consequently, you can configure your own mapping to convert the JSON to an instance of a Mendix object. This provides the possibility to decode nested JSON objects and arrays.
* RSA Key Pair generation
	- Generate public/private key pairs in Mendix (X.509 certificate and private key binary PKCS1 format)
	- Recommended not to generate new keypairs on runtime to prevent performance issues
	- Instantiate public/private keys based on known key specifications (modulus, public and private key exponent)
	- Key pairs will be persisted in the database (necessary for binary storage). Pay attention to security
	- Convert PEM certificate format (BASE64 String) to DER format (binary). Private key PKCS1 and PKCS8 supported.

## Not supported
* Algorithms
	- ECDSA
* Encoding public claims containing an array of values

## Logging
* The JWT Log node is available for more information.

## Dependencies
The JWT module implements the auth0/java-jwt library, which has the following dependencies that are included in the module package:
* com.fasterxml.jackson.core/jackson-databind
	- com.fasterxml.jackson.core/jackson-annotations
	- com.fasterxml.jackson.core/jackson-core
* commons-codec/commons-codec
* org.bouncycastle/bcpkix-jdk18on
* org.bouncycastle/bcprov-jdk18on
* org.bouncycastle/bcutil-jdk18on


Dependency conflicts have been reported in combination with org.apache.servicemix.bundles.commons-codec-1.3.0.

# Development notes
* Functionality is tested using the Mendix UnitTesting module. The tests are included in the JWTTest module.
* Use Git Flow. For contributions, fork the repository and issue a pull request to the develop branch

[1]: docs/JWT.png

# Secrets Provider

Overview
--------

Implementation of different secrets providers.

### Features

#### AWS Secret provider:
Provide secrets from AWS Secret manager (https://aws.amazon.com/secrets-manager/).

***Class:*** ```io.confluent.secrets.aws.AWSSecretProvider```

***Usage:*** ```${<Provider name>:<Secret Name>:<Secret Value>}```

##### Sample configuration

```AWS_ACCESS_KEY_ID``` and ```AWS_SECRET_ACCESS_KEY``` must be specified as environment variable. 
```
config.providers=aws

config.providers.aws.class=io.confluent.secrets.aws.AWSSecretProvider
config.providers.aws.param.region=us-east-1
```

##### Sample usage
Assuming the MySQLPassword is stored in MySecrets store in AWS Secret Manager.

```
mysql.password=${aws:MySecrets:MySQLPassword}
```

#### Cyberark (work in progress):
#### Vault:

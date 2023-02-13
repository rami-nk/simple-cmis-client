<div align="center">

# Simple CMIS-Client

*Implementation of a simple CMIS-client connecting to an in-memory CMIS-server.*

[Getting started](#getting-started)

</div>

## Getting started

1. Start in-memory CMIS-server:
```shell
git clone https://github.com/rami-nk/simple-cmis-client.git
cd simple-cmis-client
docker-compose -f stack/cmis-server/docker-compose.yml up -d
```

2. Start Application to create Client and connect to Server


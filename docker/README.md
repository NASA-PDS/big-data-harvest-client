# 🪐 Docker Image and Container for Big Data Harvest Client

## 🏃 Steps to build the docker image of the Big Data Harvest Client

#### 1. Update (if required) the following version in the `Dockerfile` with a compatible Big Data Harvest Client version.

| Variable                        | Description |
| ------------------------------- | ------------|
| big_data_harvest_client_version | The version of the Big Data Harvest Client release to be included in the docker image|

```    
# Set following argument with a compatible Big Data Harvest Client version
ARG big_data_harvest_client_version=1.0.0-SNAPSHOT
```

#### 2. Open a terminal and change the current working directory to `big-data-harvest-client/docker`.

#### 3. Build the docker image as follows.

```
docker image build -t nasapds/big-data-harvest-client .
```

## 🏃 Steps to run a docker container of the Big Data Harvest Client

#### 1. Update the Big Data Harvest Client configuration file.

* Get a copy of the `harvest-client.cfg` file from https://github.com/NASA-PDS/big-data-harvest-client/blob/main/src/main/resources/conf/harvest-client.cfg and
keep it in a local file location such as `/tmp/conf/harvest-client.cfg`.
* Update the properties such as `rmq.host`, `rmq.user` and `rmq.password` to match with your deployment environment.

#### 2. Update the Harvest job file.

* Create a Harvest job file in a local file location (E.g.: `/tmp/cfg/harvest-job-config.xml`).
* An example for a Harvest job file can be found at https://github.com/NASA-PDS/big-data-harvest-client/blob/main/src/main/resources/examples/directories.xml.
Make sure to update the `/path/to/archive` in the Harvest job file to point to a valid Harvest data directory.

#### 3. Update the following environment variables in the `run.sh`.

| Variable                   | Description |
| -------------------------- | ----------- |
| HARVEST_JOB_CONFIG_FILE    | Absolute path for the Harvest job file in the host machine (E.g.: `/tmp/cfg/harvest-job-config.xml`) |
| HARVEST_DATA_DIR           | Absolute path for the Harvest data directory in the host machine (E.g.: `/tmp/data/urn-nasa-pds-insight_rad`) |
| HARVEST_CLIENT_CONFIG_FILE | Absolute path for the Big Data Harvest Client configuration file in the host machine (E.g.: `/tmp/conf/harvest-client.cfg`) |

```    
# Update the following environment variables before executing this script

# Absolute path for the Harvest job file in the host machine (E.g.: /tmp/cfg/harvest-job-config.xml)
HARVEST_JOB_CONFIG_FILE=/tmp/cfg/harvest-job-config.xml

# Absolute path for the Harvest data directory in the host machine (E.g.: /tmp/data/urn-nasa-pds-insight_rad)
HARVEST_DATA_DIR=/tmp/data

# Absolute path for the Big Data Harvest Client configuration file in the host machine (E.g.: /tmp/conf/harvest-client.cfg)
HARVEST_CLIENT_CONFIG_FILE=/tmp/cfg/harvest-client.cfg
```

#### 4. Open a terminal and change the current working directory to `big-data-harvest-client/docker`.

#### 5. If executing for the first time, change the execution permissions of `run.sh` file as follows.

```
chmod u+x run.sh
```

#### 6. Execute the `run.sh` as follows.

```
./run.sh
```

Above steps will run a docker container of the Big Data Harvest Client.

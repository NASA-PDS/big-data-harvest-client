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
docker image build --tag nasapds/big-data-harvest-client .
```

#### 4. As an optional step, push the docker image to a container image library.

For example, follow the below steps to push the newly built image to the Docker Hub.

* Execute the following command to log into the Docker Hub with a username and password (use the username and password of https://hub.docker.com/u/nasapds).
```
docker login
```
* Push the docker image to the Docker Hub.
```
docker image push nasapds/big-data-harvest-client
```
* Visit the Docker Hub (https://hub.docker.com/u/nasapds) and make sure that the `nasapds/big-data-harvest-client` image is available, so that it can be reused by other users without building it. 


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
| HARVEST_DATA_DIR           | Absolute path for the Harvest data directory in the host machine (E.g.: `/tmp/big-data-harvest-data`). If the Big Data Harvest Client is executed with the option to download test data, then this directory will be cleaned-up and populated with test data |
| HARVEST_CLIENT_CONFIG_FILE | Absolute path for the Big Data Harvest Client configuration file in the host machine (E.g.: `/tmp/conf/harvest-client.cfg`) |

```    
# Update the following environment variables before executing this script

# Absolute path for the Harvest job file in the host machine (E.g.: /tmp/cfg/harvest-job-config.xml)
HARVEST_JOB_CONFIG_FILE=/tmp/cfg/harvest-job-config.xml

# Absolute path for the Harvest data directory in the host machine (E.g.: `/tmp/big-data-harvest-data`).
# If the Big Data Harvest Client is executed with the option to download test data, then this directory will be
# cleaned-up and populated with test data. Make sure to have the same `HARVEST_DATA_DIR` value set in the
# environment variables of the Big Data Harvest Server, Big Data Crawler Server and Big Data Harvest Client.
# Also, this `HARVEST_DATA_DIR` location should be accessible from the docker containers of the Big Data Harvest Server,
# Big Data Crawler Server and Big Data Harvest Client.
HARVEST_DATA_DIR=/tmp/big-data-harvest-data

# Absolute path for the Big Data Harvest Client configuration file in the host machine (E.g.: /tmp/conf/harvest-client.cfg)
HARVEST_CLIENT_CONFIG_FILE=/tmp/cfg/harvest-client.cfg
```

Note:

Make sure to have the same `HARVEST_DATA_DIR` value set in the environment variables of the Big Data Harvest Server,
Big Data Crawler Server and Big Data Harvest Client. Also, this `HARVEST_DATA_DIR` location should be accessible from the
docker containers of the Big Data Harvest Server, Big Data Crawler Server and Big Data Harvest Client.

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


## 🏃 Steps to run a docker container of the Big Data Harvest Client with test data

#### 1. Update (if required) the following environment variable in the `run.sh`.

| Variable          | Description |
| ----------------- | ----------- |
| TEST_DATA_URL     | URL to download the test data to harvest |

```    
# Update the following environment variable before executing this script

# URL to download the test data to Harvest (only required, if executing with test data)
TEST_DATA_URL=https://pds-gamma.jpl.nasa.gov/data/pds4/test-data/registry/urn-nasa-pds-insight_rad.tar.gz
```

#### 2. Make sure that the following environment variables are set with correct values as explained in the previous section.

| Variable                   | Description |
| -------------------------- | ----------- |
| HARVEST_JOB_CONFIG_FILE    | Absolute path for the Harvest job file in the host machine (E.g.: `/tmp/cfg/harvest-job-config.xml`) |
| HARVEST_DATA_DIR           | Absolute path for the Harvest data directory in the host machine (E.g.: `/tmp/big-data-harvest-data`). If the Big Data Harvest Client is executed with the option to download test data, then this directory will be cleaned-up and populated with test data |
| HARVEST_CLIENT_CONFIG_FILE | Absolute path for the Big Data Harvest Client configuration file in the host machine (E.g.: `/tmp/conf/harvest-client.cfg`) |


#### 3. Open a terminal and change the current working directory to `big-data-harvest-client/docker`.

#### 4. If executing for the first time, change the execution permissions of `run.sh` file as follows.

```
chmod u+x run.sh
```

#### 5. Execute the `run.sh` with the argument `test` as follows.

Warning: The following command will delete all the files in the data directory in the host machine, which is specified as the value of the 
environment variable HARVEST_DATA_DIR at the beginning of this `run.sh` script. The test data will be downloaded to the
HARVEST_DATA_DIR from the TEST_DATA_URL.

```
./run.sh test
```

#!/bin/bash

# Copyright 2022, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
# conditions and the following disclaimer in the documentation and/or other
# materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
# Laboratory, nor the names of its contributors may be used to endorse or
# promote products derived from this software without specific prior written
# permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

# --------------------------------------------------------------------------------------------------
# This script is used to execute the Registry Harvest CLI docker container with a simple command.
#
# Usage: ./run.sh [test]
#
# Optional arguments:
#     test     Download and harvest test data
#
# --------------------------------------------------------------------------------------------------

# Update the following environment variables before executing this script

# Absolute path of the Harvest job file in the host machine (E.g.: /tmp/cfg/harvest-job-config.xml)
HARVEST_JOB_CONFIG_FILE=/tmp/cfg/harvest-job-config.xml

# Absolute path of the Harvest data directory in the host machine (E.g.: `/tmp/registry-harvest-data`).
# If the Registry Harvest CLI is executed with the option to download test data, then this directory will be
# cleaned-up and populated with test data. Make sure to have the same `HARVEST_DATA_DIR` value set in the
# environment variables of the Registry Harvest Service, Registry Crawler Service and Registry Harvest CLI.
# Also, this `HARVEST_DATA_DIR` location should be accessible from the docker containers of the Registry Harvest Service,
# Registry Crawler Service and Registry Harvest CLI.
HARVEST_DATA_DIR=/tmp/registry-harvest-data

# Absolute path of the Registry Harvest CLI configuration file in the host machine (E.g.: /tmp/conf/harvest-client.cfg)
HARVEST_CLIENT_CONFIG_FILE=/tmp/cfg/harvest-client.cfg

# URL to download the test data to Harvest (only required, if executing with test data)
TEST_DATA_URL=https://pds-gamma.jpl.nasa.gov/data/pds4/test-data/registry/urn-nasa-pds-insight_rad.tar.gz

# Check if the Harvest job file exists
if [ ! -f "$HARVEST_JOB_CONFIG_FILE" ]; then
    echo "Error: The Harvest job file $HARVEST_JOB_CONFIG_FILE does not exist." \
            "Set an absolute file path of an existing Harvest job file in the $0 file" \
            "as the environment variable 'HARVEST_JOB_CONFIG_FILE'." 1>&2
    exit 1
fi

# Check if the Harvest data directory exists
if [ ! -d "$HARVEST_DATA_DIR" ]; then
    echo "Error: The Harvest data directory $HARVEST_DATA_DIR does not exist." \
            "Set an absolute directory path of an existing Harvest data directory in the $0 file" \
            "as the environment variable 'HARVEST_DATA_DIR'." 1>&2
    exit 1
fi

# Check if the Registry Harvest CLI configuration file exists
if [ ! -f "$HARVEST_CLIENT_CONFIG_FILE" ]; then
    echo "Error: The Registry Harvest CLI configuration file $HARVEST_CLIENT_CONFIG_FILE does not exist." \
            "Set an absolute file path of an existing Registry Harvest CLI configuration file in the $0 file" \
            "as the environment variable 'HARVEST_CLIENT_CONFIG_FILE'." 1>&2
    exit 1
fi

# Check if an argument is provided to this script
if [ -z "$1" ]; then

      # Execute docker container run with actual data available in the HARVEST_DATA_DIR
      docker container run --name registry-harvest-cli \
                 --rm \
                 --volume "${HARVEST_JOB_CONFIG_FILE}":/cfg/harvest-job-config.xml \
                 --volume "${HARVEST_DATA_DIR}":/data \
                 --volume "${HARVEST_CLIENT_CONFIG_FILE}":/cfg/harvest-client.cfg \
                 nasapds/registry-harvest-cli

else

    if [ "$1" = "test" ]; then

      # Execute docker container run with test data
      docker container run --name registry-harvest-cli \
                 --rm \
                 --env RUN_TESTS=true \
                 --env TEST_DATA_URL="${TEST_DATA_URL}" \
                 --volume "${HARVEST_JOB_CONFIG_FILE}":/cfg/harvest-job-config.xml \
                 --volume "${HARVEST_DATA_DIR}":/data \
                 --volume "${HARVEST_CLIENT_CONFIG_FILE}":/cfg/harvest-client.cfg \
                 nasapds/registry-harvest-cli

    else
      echo -e "Usage: $0 [test]\n" 1>&2
      echo -e "Optional argument:" 1>&2
      echo -e "\t test     Download and harvest test data\n" 1>&2
      echo -e "Execute $0 without any arguments to harvest with actual configurations and data provided" \
              "with 'HARVEST_JOB_CONFIG_FILE' and 'HARVEST_DATA_DIR' environment variables set in the $0 file.\n" 1>&2
      exit 1
    fi
fi

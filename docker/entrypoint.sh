#!/bin/sh

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

# ------------------------------------------------------------------------------
# This shell script provides an entrypoint for the Registry Harvest CLI docker image.
# ------------------------------------------------------------------------------

# Download test data to 'HARVEST_DATA_DIR', if the 'RUN_TESTS' environment variable is set to true.
if [ "$RUN_TESTS" = "true" ]; then

  # Check if the TEST_DATA_URL environment variable is set
  if [ -z "$TEST_DATA_URL" ]; then
      echo "Error: 'TEST_DATA_URL' environment variable is not set. Use docker's -e option." 1>&2
      exit 1
  fi

  rm -rf /data/*
  mkdir /data/test-data
  curl -o /tmp/harvest-test-data.tar.gz "$TEST_DATA_URL"
  tar xzf /tmp/harvest-test-data.tar.gz -C /data/test-data --strip-components 1
  rm -f /tmp/harvest-test-data.tar.gz
fi

# Execute Registry Harvest CLI
harvest-client harvest -j /cfg/harvest-job-config.xml -c /cfg/harvest-client.cfg

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

FROM openjdk:11-slim

# Set following argument with a compatible Big Data Harvest Client version
ARG big_data_harvest_client_version=1.0.0-SNAPSHOT

# Install curl
RUN apt-get update -y
RUN apt-get install curl -y

ENV BIG_DATA_HARVEST_CLIENT_BIN_PATH=https://github.com/NASA-PDS/big-data-harvest-client/releases/download/v${big_data_harvest_client_version}/big-data-harvest-client-${big_data_harvest_client_version}-bin.tar.gz

# Install Big Data Harvest Client
ADD $BIG_DATA_HARVEST_CLIENT_BIN_PATH /tmp/big-data-harvest-client-bin.tar.gz
RUN  mkdir /opt/big-data-harvest-client  \
     && tar xzf /tmp/big-data-harvest-client-bin.tar.gz  -C /opt/big-data-harvest-client --strip-components 1 \
     && rm -f /tmp/big-data-harvest-client-bin.tar.gz
ENV PATH="$PATH:/opt/big-data-harvest-client/bin"

# Execute Big Data Harvest Client
ENTRYPOINT ["harvest-client"]
# With default parameters (can be overridden)
CMD ["harvest", "-j", "/cfg/harvest-job-config.xml", "-c", "/cfg/harvest-client.cfg"]

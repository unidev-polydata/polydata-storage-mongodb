# polydata-storage-mongodb

Storage of polydata records into mongodb

Each poly storage have next fields:
 - poly info: information about poly storage 
 - poly records: polydata records stored
 - tags: generic information about tags
 - tags index: index of polydata records
 
# Build
 
 Build project `./gradlew build`
 
# Docker image build

 Docker image can be built from `polydata-storage-mongodb-rest-api`
  
 `docker build . -t polydata-storage-mongodb-rest-api:latest` 

 
 
License
=======
 
    Copyright (c) 2016,2017 Denis O <denis.o@linux.com>
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

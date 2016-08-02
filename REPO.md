# Using this repo

* Current version is defined in *version.txt*

**Installing a local SNAPSHOT**

    > ./gradlew build publishToMavenLocal

**Release a new remote SNAPSHOT**

   > git clean -xfd
   > ./gradlew artifactoryPublish

**Release a new version to Bintray**

   > git clean -xfd
   > ./gradlew release
   > git checkout HEAD~1
   > ./gradlew bintrayUpload


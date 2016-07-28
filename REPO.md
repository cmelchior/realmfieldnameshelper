# Using this repo

* Current version is defined in *version.txt*

**Build a new SNAPSHOT**

    > ./gradlew clean build

**Installing a local SNAPSHOT**

    > ./gradlew build publishToMavenLocal

**Publishing a remote SNAPSHOT**

   > ./gradlew build artifactoryPublish

**Release a new version to Bintray**

0) Make sure we build from a clean slate

   > git clean -xfd

1) Will remove SNAPSHOT. Tag the commit and bump to next patch version

   > ./gradlew release

2) Upload to bintray. TODO Find latest tag and use that as version number

   > ./gradlew bintrayUpload


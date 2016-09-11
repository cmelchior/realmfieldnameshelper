# Using this repo

* Current version is defined in *version.txt*

**Installing a local SNAPSHOT**

    > ./gradlew build publishToMavenLocal

**Release a new remote SNAPSHOT**

   > git clean -xfd
   > ./gradlew artifactoryPublish

**Release a new version to Bintray**

1) 
   > git clean -xfd
   > ./gradlew release
   > git checkout HEAD~1
   > ./gradlew bintrayUpload
   > git push
   > git push origin v1.X.Y

2) Goto https://bintray.com/cmelchior/maven/realmfieldnameshelper and release artifacts.


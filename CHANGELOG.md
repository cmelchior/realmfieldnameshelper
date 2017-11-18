### 1.2.0
- Fixed a bug preventing private fields in library projects from being generated (#25).


### 1.1.1
- Fixed a bug that crashed the annotation processor if Realm wasn't on the classpath (#14).
- Fixed crash when processing Kotlin model classes (#9).
- Fixed field names not being generated for library class references (#15).


### 1.1.0
 - Fixed a bug where hungarian notation was detected in the middle of a String.
   This resulted in deleting the wrong character at the beginning of the string.
   (Thanks @jonasrottmann).
 - Added support for linked fields one level deep.


### 1.0.0
 - Initial release

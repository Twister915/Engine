Engine
======

The backend of all Cogz based plugins!


- [x] Awesomeness
- [x] Speed
- [x] Blocks
- [ ] No bugs

[![Build Status](http://dev.tbnr.net:8080/buildStatus/icon?job=Engine-Package-master)](http://dev.tbnr.net:8080/job/Engine-Package-master/)

[![Gitter chat](https://badges.gitter.im/CogzMC/Engine.png)](https://gitter.im/CogzMC/Engine)

Compiling:
======
* Install [Maven 3](http://maven.apache.org/download.html)
* Then to compile, use 'mvn clean install'

Coding Conventions:
======

Creating a new feature:
  - git checkout -b [new-feature]
    - This is creating a new branch for the new feature that you will be working on.
  - When you have added something that you find the need to commit, commit as normal:
    - git add -A
    - git commit -m "[feature-message]"
    - You can optionally push your feature to the repository too.
  - Once you're new feature is complete, it's time to merge all your commits down into only a few!
    - git rebase -i HEAD~[number of commits in feature]
      - This will open your text editor and allow you to rebase commits.
      - You will most likely want to squash all of your commits down into a few, and make their messages descriptive!
  - When you have rebased everything, you can now open a pull request on github to merge into master.
    - The point of this is to allow other contributors a chance to look over your code before it is merged.
    - We also want to make sure that master is ALWAYS deployable. Essentially, errors on the master branch, can be             prevented by using these methods.
  - Once the code has been approved, or you believe it is time to merge, you can merge!
    - git checkout master
    - git rebase [new-feature]
      - This merges your feature into the master branch, while saving commit data associated with it.
    - Once you are 100% sure everything is done, you can delete the old branch:
      - git branch -d [new-feature]
      - Then do:
      - git push origin :[new-feature]
      - That pushes nothing to the remote branch deleting it there aswell ;)

General Conventions:
  * In general you should follow the Java Coding Standards, found [here] (http://www.oracle.com/technetwork/java/codeconv-138413.html)
  * Remember to always use lower case package names, camel case fields and methods, and in general watch the casing of       your code.
  * Always use tabs, not spaces, this will make the code very unorganized and hard to read.
  * Do not use linebreaks mid line, use the entire line, do not break, no matter the length.
  * Make sure to always format with IntelliJ IDEA, as this will get you very close to what your code should look like,       in regards to these standards.
  * Do not supress deprecated methods, at some point in time, there is a good chance that these will finally be       replaced with a different method, and are much easier to find if not supressed.
  * General file header is as follows:
```
/**
 * Created by ${USER} on ${DATE}.
 *
 * Purpose Of File:
 * 
 * Latest Change: 
 */
```
   * To Change the file header in intelij do File > settings > search file and code templates > Includes > File Header > Paste in the file header ;)
   * If you have a utility file in gearz make it implement GUtility so we can easily find them (only in gearz)
   * If you have a method you made for let's say headhunter and it is a good utility method that can be put in a utlity file in gearz then give it the annotation @GUtilityMethod
   * If you have a abstract class or something taht you think should be moved to gearz make the class implement Move2Gearz

Branch Naming:
  - Branch names should be lowercase. Ex: parties (correct) vs. Parties (incorrect)
  - Use dashes  (-) instead of spaces

How to merge a feature branch:
  - DO NOT PRESS THE BIG GREEN BUTTON
  - Checkout the feature branch and type:
     - git rebase origin/master
        - This will pull all the current changes from the master branch and merge them in. Solve any merge conflicts.
     - git push --force
        - This force pushes all the changes to the feature branch.
     - git checkout master
     - git rebase [feature-branch]
  - That should neatly merge your feature branch!

Engine
======

The repo for the Engine stuff.


- [x] Awesomeness
- [x] Speed
- [x] Blocks
- [ ] No bugs

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
    - We also want to make sure that master is ALWAYS deployable. Essentially, errors on the master branch, can be prevented by using these methods.
  - Once the code has been approved, or you believe it is time to merge, you can merge!
    - git checkout master
    - git rebase [new-feature]
      - This merges your feature into the master branch, while saving commit data associated with it.
    - Once you are 100% sure everything is done, you can delete the old branch:
      - git branch -d [new-feature]
  

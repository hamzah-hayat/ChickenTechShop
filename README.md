# Chicken's Tech Shop

A mod for Starsector that creates a special contact called Chicken, a spacer that specailises in selling tech goodies
Meeting him will create a submarket at his location that sells items such as AI cores, Colony items and even Blueprints.

## Credits

I basically hacked my way through this using a combination of the following as guides, big thanks to them

- [Starsector Modding forums (Modding Tools & Resources Thread)](https://fractalsoftworks.com/forum/index.php?topic=633.0)
- [Nexerelin](https://fractalsoftworks.com/forum/index.php?topic=9175.0)
- [Special Hullmod Upgrades](https://fractalsoftworks.com/forum/index.php?topic=25424.0)

Also Special thanks to the template provided here [Starsector Mod Template by jaghaimo](https://github.com/jaghaimo/starsector-mod)
The LICENSE_TEMPLATE.MD is for everything provided by said Template (Inital Commit - a417e4043ea1d1e01e76453c43254cd3dbb2b86e)
While LICENSE.MD is for my work (Everything After)
Feel free to use whatever you see here if you find it useful

## Template README

The original readme can be found [here](https://github.com/jaghaimo/starsector-mod/blob/master/README.md)

## Gradle Commands

gradlew build - Builds the .jar file and moves files from assets into their respective folders
gradlew release - Creates a zipped release file

## Release Commands

Much easier to use the Github workflow for releases, the steps are:

1. Update Version number in ChickenTechShop.version and assets\mod_info.json to the new version number
2. Run the git commands:
```
   git commit -m "My first release" # you can just `git commit` and type a body of release as well
   git push origin master           # Send the commit to your repo (no release yet)
   git tag 1.0.0                    # No release yet, it's only local now
   git push --tags origin master    # Release is happening now
```
3. Let Github actions create the release, note the last commit will become the release body

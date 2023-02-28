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
























# Starsector mod template

This is a basic Github template to create a working Starsector mod.
Under the hood it uses a Gradle automation tool to fetch dependencies, compile source code and create release ZIPs.

## Using the template

1. Have Java SDK installed, anything 7 or above will work. I'd suggest OpenJDK 11.
1. Pick a mod name (can have spaces, e.g. "My Starsector Mod") and mod identifier (cannot have spaces, only A-z,
"mymodid", try to make it unique across mod-verse).
1. Click "Use this template", use your mod identifier as "Repository name".

## Setting up an IDE (VSCode)

1. Download and install VSCode, Git, and Java Development Kit (if you haven't yet).
1. Open VSCode, select "Clone Repository" and pass the URL of your repository.
1. Install following extensions in VSCode: Extension Pack for Java, Gradle Tasks.
1. Consider using SSH Keys instead of password authentication for communicating with Github (not covered here).

This template comes with debugging and hot code swap configuration for VSCode (see `.vscode/launch.json` for details).
You are free to use any other IDE. It may work out of the box if it supports Gradle, or you may need to set it up.
Refer to Starsector forums for details.

If you have a recent Windows 10 build you can use `winget` tool to automate step 1:
```sh
winget install --id microsoft.visualstudiocode
winget install --id git.git
winget install openjdk
```

## Making your first mod

Clone this repository to your `Starsector/mods` folder (already done if you followed "Setting up an IDE (VSCode)".
Edit `assets/mod_info.json` accordingly. Rename `src/main/java/mymodid/MymodMod.java` accordingly: `mymodid` folder name
and package name in Java file should match your mod id from `mod_info.json`. You may consider renaming `MymodMod.java`
to something more sensible, like `<id from mod_info.json>Mod.java`. Make sure your `mod_info.json` matches reality
(specifically `jars` and `modPlugin`).

## Building

1. Run `./gradlew build` or `gradlew.bat build` (depending on your operating system) - you will get "empty" jar (empty
because there is no code yet)

## Packaging

1. (optional) Create additional content in `assets/`:
   1. `data` and `graphics` folders (if any) with their content,
   1. any other files you want to bundle.
1. Run `./gradlew release` (`mod_info.json`'s version will be used)

## Releasing (automatic)

Tag a commit and push it - tag name will become a release name, and commit message will become a release message.

```sh
git commit -m "My first release" # you can just `git commit` and type a body of release as well
git push origin master           # Send the commit to your repo (no release yet)
git tag 1.0.0                    # No release yet, it's only local now
git push --tags origin master    # Release is happening now
```

## Releasing (manual)

1. Click "Create a release" (either from main page of your repository or from "Releases" tab).
1. Name a tag, add title, and add description (optional, usually a list of changes made in this version).
1. Add zip file you created in Packaging section and click "Publish release".

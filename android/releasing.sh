#!/usr/bin/env bash
set -e
ROOT_DIR="$(git rev-parse --show-toplevel)"
ANDROID_DIR="$ROOT_DIR/android"

# Change the version in `gradle.properties` to a non-SNAPSHOT version
sed -i '' 's|-SNAPSHOT||' $ANDROID_DIR/gradle.properties
NEW_VERSION=$(grep -Eo "\d+\.\d+\.\d+" $ANDROID_DIR/gradle.properties)
echo "Preparing to release: $NEW_VERSION"

# Update the `README.md` with the new version
OLD_VERSION=$(grep -iE "'com.uber.rib:rib-" $ROOT_DIR/README.md | grep -Eo "\d+\.\d+\.\d+" | sort | uniq)
find $ROOT_DIR -type f -name 'README.md' | grep -v 'ios' | xargs sed -i '' "s|$OLD_VERSION|$NEW_VERSION|g"

# Update the `CHANGELOG.md` for the impending release
vim $ROOT_DIR/CHANGELOG.md

# Commit new version prep
git commit -am "Prepare for release $NEW_VERSION"

# Tag new version
git tag -a "v$NEW_VERSION" -m "Version $NEW_VERSION"

# Clean build, push to sonatype, and release the repos
echo "Building and releasing..."
pushd $ANDROID_DIR
./gradlew clean publish --no-daemon --no-parallel && ./gradlew closeAndReleaseRepository
popd

# Prepare for next snapshot
NEXT_PATCH=$(expr $(echo $NEW_VERSION | sed 's|^.*\.||') + 1)
NEXT_VERSION="$(echo $NEW_VERSION | grep -o '^.*\.')$NEXT_PATCH-SNAPSHOT"
echo "Next dev version is $NEXT_VERSION"
sed -i '' "s|$NEW_VERSION|$NEXT_VERSION|" $ANDROID_DIR/gradle.properties

# Commit next snapshot prep
git commit -am "Prepare next development version"

# Push to remote
REMOTE=$(git remote -v | grep -Ei "uber/ribs.*push" | awk '{print $1}')
echo "Pushing to remote: $REMOTE"
git push $REMOTE && git push $REMOTE --tags

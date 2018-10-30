#!/usr/bin/env sh

# Configuration
MY_XCODE_PATH="$(xcode-select -p)"
XCODE_SELECT_PATH_SUFFIX='/Contents/Developer' # xcode-select -p includes this after the path we need to append templates to
TEMPLATE_DIR_PATH_SUFFIX='/Templates/File Templates/RIBs'
XCODE_TEMPLATE_DIR="$(echo ${MY_XCODE_PATH/$XCODE_SELECT_PATH_SUFFIX/$TEMPLATE_DIR_PATH_SUFFIX})"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Copy RIBs file templates into the local RIBs template directory
xcodeTemplate () {
  echo "==> Copying up RIBs Xcode file templates..."

  if [ -d "$XCODE_TEMPLATE_DIR" ]; then
    rm -R "$XCODE_TEMPLATE_DIR"
  fi
  mkdir -p "$XCODE_TEMPLATE_DIR"

  cp -R $SCRIPT_DIR/*.xctemplate "$XCODE_TEMPLATE_DIR"
  cp -R $SCRIPT_DIR/RIB.xctemplate/ownsView/* "$XCODE_TEMPLATE_DIR/RIB.xctemplate/ownsViewwithXIB/"
  cp -R $SCRIPT_DIR/RIB.xctemplate/ownsView/* "$XCODE_TEMPLATE_DIR/RIB.xctemplate/ownsViewwithStoryboard/"
}

xcodeTemplate

echo "==> ... success!"
echo "==> RIBs have been set up. In Xcode, select 'New File...' to use RIBs templates."

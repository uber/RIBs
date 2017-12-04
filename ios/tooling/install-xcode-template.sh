#!/usr/bin/env sh

# Configuration
XCODE_TEMPLATE_DIR=$HOME'/Library/Developer/Xcode/Templates/File Templates/RIBs'
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

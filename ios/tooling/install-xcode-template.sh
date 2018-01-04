#!/usr/bin/env sh

# Configuration
XCODE_FILE_TEMPLATE_DIR=$HOME'/Library/Developer/Xcode/Templates/File Templates/RIBs'
XCODE_IOS_PROJECT_TEMPLATE_DIR=$HOME'/Library/Developer/Xcode/Templates/Project Templates/iOS/RIBs'
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Copy RIBs file templates into the local RIBs template directory
xcodeTemplate () {
  echo "==> Copying up RIBs Xcode file templates..."

  if [ -d "$XCODE_FILE_TEMPLATE_DIR" ]; then
    rm -R "$XCODE_FILE_TEMPLATE_DIR"
  fi
  mkdir -p "$XCODE_FILE_TEMPLATE_DIR"

  if [ -d "$XCODE_IOS_PROJECT_TEMPLATE_DIR" ]; then
    rm -R "$XCODE_IOS_PROJECT_TEMPLATE_DIR"
  fi
  mkdir -p "$XCODE_IOS_PROJECT_TEMPLATE_DIR"

  # File Templates
  cp -R $SCRIPT_DIR/File\ Templates/*.xctemplate "$XCODE_FILE_TEMPLATE_DIR"
  cp -R $SCRIPT_DIR/File\ Templates/RIB.xctemplate/ownsView/* "$XCODE_FILE_TEMPLATE_DIR/RIB.xctemplate/ownsViewwithXIB/"
  cp -R $SCRIPT_DIR/File\ Templates/RIB.xctemplate/ownsView/* "$XCODE_FILE_TEMPLATE_DIR/RIB.xctemplate/ownsViewwithStoryboard/"

  # Project Templates
  cp -R $SCRIPT_DIR/Project\ Templates/RIBs\ App.xctemplate "$XCODE_IOS_PROJECT_TEMPLATE_DIR/RIBs App.xctemplate"
  cp -R $SCRIPT_DIR/File\ Templates/RIB.xctemplate/ownsView/___FILEBASENAME___ViewController.swift "$XCODE_IOS_PROJECT_TEMPLATE_DIR/RIBs App.xctemplate/ViewController.swift"
  cp -R $SCRIPT_DIR/File\ Templates/RIB.xctemplate/ownsView/___FILEBASENAME___Interactor.swift "$XCODE_IOS_PROJECT_TEMPLATE_DIR/RIBs App.xctemplate/Interactor.swift"
}

xcodeTemplate

echo "==> ... success!"
echo "==> RIBs have been set up. In Xcode, select 'New File...' to use RIBs templates."

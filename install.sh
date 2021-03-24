#!/bin/bash

CUR_VERSION="$(kattlo -V)"
NEW_VERSION="$(curl -s https://api.github.com/repos/kattlo/snip/releases/latest | grep tag_name | cut -d':' -f2 | cut -d'"' -f2 | cut -d'v' -f2)"
echo "Current Version: $CUR_VERSION => New Version: $NEW_VERSION"

if [ "$NEW_VERSION" != "$CUR_VERSION" ]; then

  echo "Installing version $NEW_VERSION"

  pushd /tmp/

  curl -s https://api.github.com/repos/kattlo/kattlo-cli/releases/latest \
  | grep "browser_download_url.*snip-.*-linux" \
  | cut -d ":" -f 2,3 \
  | tr -d \" \
  | wget -qi -

  downloaded="$(find . -name "snip-v*-linux" 2>/dev/null)"

  chmod +x $downloaded

  sudo mv $downloaded /usr/local/sbin/kattlo

  popd

  location="$(which kattlo)"
  echo "Snip binary location: $location"

  version="$(kattlo -V)"
  echo "New Snip binary version installed!: $version"

else
  echo Latest version already installed
fi
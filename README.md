# lr2ir-read-only

An LR2IR read-only IR library for beatoraja.

> Note: While they both serve the same purpose, this project is unrelated to [rekidai-info's](https://github.com/rekidai-info) [LR2IR-Read-Only project](https://github.com/rekidai-info/LR2IR-Read-Only).

## Features
- Ranking display
- Rivals fetching and display

## Not working (and never will)
- Course support
- Table support
- IR score submission

## Usage
- Download the latest release from the [Releases page](github.com/SayakaIsBaka/lr2ir-read-only/releases)
- Put the downloaded JAR file into beatoraja's `ir` folder
- On beatoraja's configuration, put your LR2IR ID as the User ID (eg. `12345`) and anything as the password (eg. `test`). The password field is never used since LR2IR is only being accessed as read-only.

The User ID is only used to retrieve rivals. If you only want to display rankings and don't have an LR2IR account, you can put any User ID as long as it is valid (`111111` for example).
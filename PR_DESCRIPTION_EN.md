# PR Title

Forge 1.20.1 port of Tails

# PR Body

Hello Kihira,

This pull request proposes an unofficial port of **Tails** to **Minecraft Forge 1.20.1**.

The goal of this port has been to preserve the original feel of the mod as closely as possible while updating the rendering and GUI code to modern Forge APIs.

## Included in this PR

- ported the mod to Minecraft Forge `1.20.1`
- restored dynamic tint texture generation for modern rendering
- restored player-attached rendering behavior for tails, ears, wings, and muzzle
- updated the editor GUI to more closely match the original layout and interaction flow
- added live preview updates while editing
- cleaned up localization keys and missing translations
- kept the project publicly reviewable instead of sharing private binaries

## Notes

- this is being submitted as a public code review path instead of a Drive upload
- if you prefer, I can split this into smaller PRs by subsystem
- if you want a different target branch than `develop`, I can rebase and reopen it there

## Testing

- project compiles successfully with Gradle on Forge `1.20.1`
- major in-game checks have focused on editor rendering, part placement, and texture generation

## Screenshots

Replace `<forge-1.20.1-port>` with the branch you push before pasting this into GitHub if you want image previews in the PR body.

### In-Game Showcase

![In-game front showcase](https://raw.githubusercontent.com/AkashiroSku/Tails/<branch-name>/docs/images/pr/in-game-front-showcase.png)

![In-game shark tail showcase](https://raw.githubusercontent.com/AkashiroSku/Tails/<branch-name>/docs/images/pr/in-game-shark-tail.png)

### Editor Showcase

![Editor tail selection](https://raw.githubusercontent.com/AkashiroSku/Tails/<branch-name>/docs/images/pr/editor-tail-selection.png)

![Editor tint preview](https://raw.githubusercontent.com/AkashiroSku/Tails/<branch-name>/docs/images/pr/editor-tint-preview.png)

## Attribution

This remains Kihira's original mod. This PR is only an unofficial port proposal for review.

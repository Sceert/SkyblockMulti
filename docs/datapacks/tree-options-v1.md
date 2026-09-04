# Tree options data format (schema 1)

Tree definitions are server data resources stored at:

`data/<namespace>/skyblockmulti/tree_options/<path>.json`

The resource path determines the stable tree ID. For example,
`data/example/skyblockmulti/tree_options/redwood.json` defines
`example:redwood`.

```json
{
  "schema_version": 1,
  "display": {
    "name": { "translate": "example.tree.redwood" },
    "description": { "translate": "example.tree.redwood.description" },
    "icon": { "item": "example:redwood_sapling" }
  },
  "placement": {
    "type": "skyblockmulti:sapling",
    "item": "example:redwood_sapling",
    "pattern": "single"
  },
  "categories": ["example:large"],
  "enabled_by_default": true,
  "requirements": {
    "mods": ["examplemod"]
  }
}
```

Supported placements in schema 1:

- `skyblockmulti:sapling`, with `single` or `two_by_two` pattern.
- `skyblockmulti:azalea`, with an optional `flowering` boolean.
- `skyblockmulti:structure`, with `structure`, optional three-integer
  `offset`, and `rotation` set to `none` or `random`.

Sapling and azalea placements require empty target blocks. A 2x2 sapling uses
the declared origin plus its east, south, and southeast neighbours. Structure
templates are limited to 64 blocks on each axis and are only placed while all
chunks covered by their rotated bounding box are already loaded.

`description`, `categories`, `requirements`, and `enabled_by_default` are
optional. Names can use either `{ "translate": "key" }` or literal
`{ "text": "Name" }` values.

There are no random weights. Every enabled and valid tree participates exactly
once in random selection and therefore has the same probability.

Invalid definitions are skipped with a server log warning. Definitions whose
required mods are absent are also skipped without preventing the server from
loading.

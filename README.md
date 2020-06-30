Commands:
- Core
    - /prefix (Use for requesting, managing and approving prefixes)
- Bungee
    - /bprefix (Use for reloading prefixes)
- Spigot
    - /sprefix (Use for opening GUI instead of using commands)

Permissions:
- fprefix
    - fprefix.command
        - fprefix.command.prefix
            - fprefix.command.prefix.others
            - fprefix.command.prefix.color
            - fprefix.command.prefix.exceedLength
            - fprefix.command.prefix.allowSpaces
        - fprefix.command.reload
        - fprefix.command.gui
        - fprefix.command.request
        - fprefix.command.approve
        - fprefix.command.deny
        - fprefix.command.list
        - fprefix.command.refresh
```json5
{
  "debugMode": false, // If true, will produce debug logging that can be helpful in finding problems. 
  "messageLocale": { // Message locale used in both spigot & bungee
    "approvedPrefixMail": "&aYour prefix &2${prefix}&a has been approved",
    "deniedPrefixMail": "&cYour prefix &4${prefix}&c has been denied",
    "prefixApproved": "&aReviewed and &3approved &a${player}'s prefix",
    "prefixDenied": "&aReviewed and &cdenied &a${player}'s prefix",
    "reviewInProcess": "&7Your prefix is being sent to review now.",
    "prefixListMessage": "&3-&b${player} &e= &r${prefix} ",
    "prefixApproveButton": "&a&l[&3&lAPPROVE&a&l]",
    "prefixApproveButtonHover": "Approve ${player}'s prefix",
    "prefixDenyButton": "&c&l[&4&lDeny&c&l]",
    "prefixDenyButtonHover": "Deny ${player}'s prefix",
    "prefixLengthExceeded": "&cYour prefix has exceeded the length which is: &6${length}",
    "queueIsEmpty": "&8Prefix queue is empty",
    "queueDoesNotContain": "&cThe queue does contain ${player}"
  },
  "prefixLength": 16, // The maximum length of a prefix
  "includeColorCodesInLength": false, // Should color codes affect prefix length
  "databaseAuthInfo": {
    "username": "root",
    "password": "password",
    "port": "3306",
    "urlHost": "localhost",
    "database": "db",
    "cachePrepStmts": true, // Change only if necessary
    "prepStmtCacheSize": 250, // Change only if necessary
    "prepStmtCacheSqlLimit": 2048, // Change only if necessary
    "useServerPrepStmts": true, // Change only if necessary
    "mysqlDriver": "mariadb_hikari" // MariaDB should work for MySQL, but if otherwise you may change the value to "mysql_hikari"
  },
  "storageTypes": "JSON", // Possible values: MYSQL, JSON, YAML, Plugin (Spigot Only, uses Bungeecord's database)
  "appendPrefixRequestSuffix": " &r", // Adds suffix to a prefix requested, though it will be shown once applied to the player.
  "writeLogs": true, // Should logs be written?
  "guiLocale": { // Locale for Spigot GUI
    "prefixMaterial": "NAME_TAG",
    "approveMaterial": "EMERALD_BLOCK",
    "denyMaterial": "REDSTONE_BLOCK",
    "exitInventoryMaterial": "BARRIER",
    "silenceMaterial": "NOTE_BLOCK",
    "reloadButtonMaterial": "STICK",
    "currentPageButtonMaterial": "PAPER",
    "scrollPageButton": "ARROW",
    "prefixListGUITitle": "&9Prefix Approval Queue",
    "prefixListGUIApproveTitle": "&9Prefix Approval for ${player}",
    "prefixItemName": "&3${player}",
    "prefixItemLore": "${prefix}",
    "reloadListButton": "&aReload",
    "silentButton": "&cSilent: ${silent}",
    "approveButton": "&aApprove",
    "denyButton": "&cDeny",
    "closeInventoryButton": "&cExit",
    "nextPageButton": "&3Next page",
    "previousPageButton": "&ePrevious page",
    "currentPageButton": "&6Current page: &a${page}",
    "prefixManagedByOther": "&cPrefix has been approved/denied by a different staff member or removed abruptly from database."
  },
  "prefixQueueGuiSize": {
    "rows": 6,
    "columns": 9
  }
}
```
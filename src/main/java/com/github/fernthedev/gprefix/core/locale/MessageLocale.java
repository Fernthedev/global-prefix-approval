package com.github.fernthedev.gprefix.core.locale;

import lombok.Getter;

@Getter
public class MessageLocale {

    private String approvedPrefixMail = "&aYour prefix &r${prefix}&a has been approved";
    private String deniedPrefixMail = "&cYour prefix &r${prefix}&c has been denied";

    private String prefixApproved = "&aReviewed and &3approved &a${player}'s prefix";
    private String prefixDenied = "&aReviewed and &cdenied &a${player}'s prefix";

    private String reviewInProcess = "&7Your prefix is being sent to review now.";
    private String prefixListMessage = " &3-&b${player} &e= &r${prefix} ";
    private String prefixApproveButton = "&a&l[&2&lAPPROVE&a&l]";
    private String prefixApproveButtonHover = "Approve ${player}'s prefix";
    private String prefixDenyButton = "&c&l[&4&lDeny&c&l]";
    private String prefixDenyButtonHover = "Deny ${player}'s prefix";

    private String prefixLengthExceeded = "&cYour prefix has exceeded the length which is: &6${length}";
    private String queueIsEmpty = "&8Prefix queue is empty";
    private String queueDoesNotContain = "&cThe queue does contain ${player}";

}

# VirtualCurrency - NeoForge 1.21.1
 
**VirtualCurrency** is a NeoForge mod for Minecraft 1.21.1 that introduces a server-side virtual currency system. The currency is stored per-player, synchronized to the client, and displayed on the HUD above the health bar. The mod also includes commands, an API, and integration support for other mods.

## Features

- Persistent virtual currency per player
- Server-client sync using NeoForge's payload system
- Currency displayed on the in-game HUD
- Currency API for other mods to integrate
- Custom commands to manage balances
- "Moneybag" item to store and redeem physical currency tokens

## Commands
Some of the commands require operator permissions (or can be configured via permissions system):
- /currency get <playerName> - View your current balance
- /currency add <amount> - Add currency to your balance (OP)
- /currency remove <amount> - Remove currency from your balance (OP)
- /currency set <amount> - Set your balance to a specific amount (OP)
- /currency withdraw <amount> - Withdraw currency into a Moneybag item

## API Usage

You can integrate VirtualCurrency into your mod using the provided API:

```java
import net.aimless.virtualcurrency.api.CurrencyAPI;
import net.minecraft.server.level.ServerPlayer;

// Get player's balance
int balance = CurrencyAPI.getBalance(player);

// Set player's balance
CurrencyAPI.setBalance(player, 1000);

// Add or remove currency
CurrencyAPI.addBalance(player, 500);
CurrencyAPI.removeBalance(player, 250);
```
Note: All methods automatically sync the new balance with the client.

## Requirements
 - Minecraft 1.21.1
 - NeoForge 21.1.169 or newer

## Developer Notes
You can use CurrencyAPI in your own NeoForge mod by depending on this mod and calling its methods directly. If you’d like to register custom integrations (e.g., shops, quests, trades), feel free to create issues or PRs!

## TODO

- [ ] Config file to enable/disable features
- [x] Sync currency to client
- [x] Display currency on HUD

MIT License © 2025 [AiMLESS2k]

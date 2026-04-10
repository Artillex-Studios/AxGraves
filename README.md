**Bug Reports and Feature Requests:** https://github.com/Artillex-Studios/Issues

**Support:** https://dc.artillex-studios.com/

![axgraves-banner](https://github.com/Artillex-Studios/AxGraves/assets/52270269/771b2e74-58e4-4128-b822-099ec20802b0)

---

## Added Features

### Graves GUI
- `/graves` opens an inventory GUI listing all your active graves
- Each grave is shown as a compass with its world, coordinates, time remaining and teleport cost in the lore
- Click a grave to teleport to it
- Shows a barrier item with a message if you have no active graves
- All text is configurable in `messages.yml` under the `gui:` section

### Teleport Cost (Vault)
- Requires Vault and an economy plugin
- Set `teleport-cost.enabled: true` in `config.yml` to charge players when teleporting to a grave
- Configure the amount with `teleport-cost.amount`
- Players with the `axgraves.tp.free` permission are not charged

### Grave Protection
- Graves are protected for a configurable time after death — only the owner can open them during this window
- After the protection period expires, anyone can open the grave (unless `interact-only-own` is also enabled)
- Players with `axgraves.admin` bypass protection entirely
- Set the global default with `grave-protection-seconds` in `config.yml` (default: 300 seconds)
- Override per player with `axgraves.protection.<seconds>`
- Use `axgraves.protection.0` to disable protection for a specific player/group
- The protection time is resolved at death and stored with the grave
- Message shown to blocked players is configurable via `interact.grave-protected` in `messages.yml`

**Example permissions:**
```
axgraves.protection.0     # no protection
axgraves.protection.300   # 5 minutes (default)
axgraves.protection.600   # 10 minutes
axgraves.protection.1800  # 30 minutes
```

### Permission-Based Grave Despawn Time
- Give players the permission `axgraves.despawn.<seconds>` to override the global despawn time for their graves
- Use `axgraves.despawn.-1` for graves that never despawn
- If a player has multiple matching permissions, the highest value wins
- Falls back to `despawn-time-seconds` in `config.yml` if no permission matches
- The despawn time is resolved at the moment of death and stored with the grave, so it survives server restarts

**Example permissions:**
```
axgraves.despawn.600    # 10 minutes
axgraves.despawn.1800   # 30 minutes
axgraves.despawn.21600  # 6 hours
axgraves.despawn.-1     # never despawn
```

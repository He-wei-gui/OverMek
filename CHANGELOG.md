# Changelog / 更新日志

## v1.1.0 — 2026-05-17

### 中文

**新增 / Improvements**
- 裂变反应堆显示侧倍率：玩家 GUI 上能直接看到 `actualBurnRate`、`heatingRate` 按 efficiency 倍数提升，不再「插板看不出来变化」。
- 感应矩阵主面板的 `lastInput`、`lastOutput` 也按 transfer 倍率做显示乘法，与 stats 标签的 `transferCap` 保持一致。
- 所有多方块 mixin 统一加入 `tick HEAD` host 自动重同步逻辑（Matrix / SPS / Fission）。玩家把电路板放进任意一块 Casing 都能正确生效。
- SPS active 检测放宽：能量持续进入但本 tick 进度未累计到 1 mB 时也算 active，warmup 与额外加成不再被吞掉。
- SPS 额外加成基线兜底：`lastProcessed = 0` 但能量正在流入时，按 `lastReceivedEnergy / spsEnergyPerInput` 推导基线，避免「正在受能但板子不发力」。

**修复 / Bug Fixes**
- **裂变反应堆温度跳变**：插入板子的瞬间，温度不再有任何视觉跳变。原版 `setHeatCapacity(_, true)` 只保留环境温度，对开过机有残余热量的反应堆仍会漂移；现在改为手动按 `targetCap / oldCap` 比例缩放 storedHeat，严格保持温度恒定。
- **裂变反应堆 damage 修复被卡**：停堆冷却阶段（`active = false`）也能让 stability 加成持续修复 reactor damage。
- **SPS 插板闪退**：`FloatingLong.create(double)` 会把 `[0.99995, 1.00005)` 量化成零，预热前 1~3 tick 时 `energyUsageMultiplier ≈ 0.99998` 触发 `divide by zero` 崩溃。新增 epsilon 死区检测（差值 < 0.0005 视为等于 1.0），并加 `try/catch` 兜底。
- **多方块电路板「孤儿」问题**：玩家把板子放进非 host casing 时，旧 migration 只在 source → host 之间迁移，第三个 casing 上的板子永远不被发现。`BoardHostResolver.migrateFromAnyMember` 现在扫描整个 multiblock 的所有同族 casing，集中迁移到 host。

**SPS 重平衡**
- `throughputMultiplier`: 4.6 → 6.0（产出加快）
- `energyUsageMultiplier`: 0.68 → 0.95（显示能耗不再被夸大 +47%，只 +5%）
- `pressureMultiplier`: 4.2 → 3.0（钋副产浪费减少）

**注意**：升级后如果你的 `config/overmek-common.toml` 已存在，旧的 SPS 数值会被沿用。要应用新平衡值，删除该 toml 让其重建，或手动改 `[sps]` 段的三项。

---

### English

**Improvements**
- Fission Reactor display-side multipliers: `actualBurnRate` and `heatingRate` are now multiplied by efficiency in the GUI, so the board's effect is immediately visible instead of "looks identical with or without board."
- Induction Matrix main panel `lastInput` / `lastOutput` are also display-multiplied by the transfer multiplier, matching the stats-tab `transferCap` behavior.
- All multiblock mixins (Matrix / SPS / Fission) now share a `tick HEAD` host auto-resync so installing the board into any casing of the structure works correctly.
- SPS active detection widened: when energy is flowing in but this-tick progress hasn't accumulated to 1 mB, the SPS still counts as active, so warmup and bonus processing aren't dropped.
- SPS bonus baseline fallback: when `lastProcessed = 0` but energy is being received, baseline is derived from `lastReceivedEnergy / spsEnergyPerInput`, preventing "energy flowing but board does nothing."

**Bug Fixes**
- **Fission reactor temperature jump**: Inserting a board no longer causes any visible temperature jump. Vanilla `setHeatCapacity(_, true)` only preserves ambient temperature and drifts on a reactor with residual heat; we now manually scale `storedHeat` by `targetCap / oldCap` to keep temperature strictly constant.
- **Fission damage repair stuck**: Stability-driven damage repair now also works during cooldown (`active = false`), so a shut-down hot reactor still heals.
- **SPS insertion crash**: `FloatingLong.create(double)` quantizes `[0.99995, 1.00005)` to zero. During the first 1–3 warmup ticks the `energyUsageMultiplier` is `≈ 0.99998` → `divide by zero` crash. Fixed with an epsilon dead-zone check (treat values within 0.0005 of 1.0 as no-op) plus `try/catch` safety net.
- **Multiblock board "orphan" problem**: When the player inserted a board into a non-host casing, the old migration only moved boards from `source → host`. A board on a third casing was never found. `BoardHostResolver.migrateFromAnyMember` now scans every same-family member of the multiblock and centralizes any found board on the host.

**SPS Rebalance**
- `throughputMultiplier`: 4.6 → 6.0 (faster antimatter production)
- `energyUsageMultiplier`: 0.68 → 0.95 (displayed energy no longer inflated by +47%, now only +5%)
- `pressureMultiplier`: 4.2 → 3.0 (less polonium waste)

**Note**: If your `config/overmek-common.toml` already exists, the old SPS values will be kept. To apply the new balance, delete the toml so it regenerates, or manually edit the three entries under the `[sps]` section.

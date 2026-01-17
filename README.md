# GregE-core

**GregE-core** is a core mod developed for the **GregE** modpack. It introduces high-tier multiblock machinery, advanced materials, and unique energy production systems to the GregTech ecosystem.

---

## 🏗 Developer Info

> [!IMPORTANT]  
> **No Default Recipes:** All added items and machines do not come with recipes. You must add them yourself via KubeJS, GroovyScript, or other configuration methods.

* **GTCEu Version:** Optimized for `7.2.1`.
* **Standalone Functionality:** All machines are designed to work independently of the GregE modpack if recipes are provided.
* **Modpack Usage:** You are free to use this core mod in your own modpacks.

---

## 🛠 Features

### Advanced Blast Furnaces (EBF)
* **Accelerated EBF:** Faster than the standard EBF, featuring its own set of custom coils.
* **Giant Accelerated EBF:** A massive multiblock that requires **Deionized Water** to operate. Includes a dedicated tier of custom coils.
* **Learning Accelerated EBF:** A specialized late-game machine that "levels up." It gains energy reductions, speed boosts, and increased parallelism based on the total volume of items processed.

### Advanced Machinery
* **Giant Chemical Reactor:** A versatile multiblock that gains unique abilities and bonuses depending on the **Bacteria** used within the process.
* **Enhanced Fusion Reactor:** A heat-management-focused reactor. Maintain specific heat levels to unlock boosted fusion recipes.
    * **Recipe Type:** `advanced_fusion`
    * **Usage:** Add `.addData('heat_level', X)` to recipes (where X is a whole number like 1, 2, 3...).

### Dyson Swarm System
* **Launcher & Collector:** Launch solar sails into space to generate massive amounts of energy.
* **Rewards:** Large-scale launches are rewarded with increased energy yields.
* **Recipe Types:** Use `launch_sails` and `get_solar_sail_energy`.

### Content & Materials
* Adds custom materials and fluids.
* Includes new Engine and Firebox blocks.

# DCTweaks-Continued
Additional features and performance enhancements for Dimensional Control. Forked from https://www.curseforge.com/minecraft/mc-mods/dimensional-control-tweaks
#### Original DCTweaks Changes
Fix - The second layer in a GenLayerCombine did not get its seed initialized, resulting in it generating the same regardless of world seed.<br>
Fix - GenLayerTouching would stop checking further data when the minimum bordering biome condition was met, regardless of if the chance parameter prevented it from replacing the biome.<br>
Performance - GenLayerCombine and GenLayerTouching now use hashmaps to store and get combinations instead of looping over all combinations, substantially increasing performance for large data sets.
#### Additional Changes
##### Features
1) GenLayerClamp - allows for biome replacements based on x and z world coordinates. See documentation for details
2) GenLayerZoomSubstitute - a variant of the zoom layer that allows users to substitute a given biome for another when placed by magnification. Used to prevent some biomes from growing larger during magnification
3) GenLayerVoronoiZoomSubstitute - the same as GenLayerZoomSubstitute, but for voronoi zoom
##### Performance
1) GenLayerTouching rules now store whitelists and blacklists as hashsets, improving performance for large data sets.<br>
2) Initially, GenLayerTouching would loop over all neighbors, even after meeting the replace requirements for a rule. Now it exits early if sufficient neighbors meet the whitelist/blacklist requirements, or if enough neighbors don't that it is impossible for the remainder to do so.

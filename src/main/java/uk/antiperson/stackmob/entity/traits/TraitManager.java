package uk.antiperson.stackmob.entity.traits;

import org.bukkit.entity.LivingEntity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;
import uk.antiperson.stackmob.entity.traits.trait.*;
import uk.antiperson.stackmob.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

public class TraitManager {

    private final HashSet<Trait<LivingEntity>> traits;
    private final StackMob sm;
    public TraitManager(StackMob sm) {
        this.sm = sm;
        this.traits = new HashSet<>();
    }

    public void registerTraits() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        registerTrait(SheepColor.class);
        registerTrait(SheepShear.class);
        registerTrait(HorseColor.class);
        registerTrait(SlimeSize.class);
        registerTrait(LlamaColor.class);
        registerTrait(ParrotVariant.class);
        registerTrait(CatType.class);
        registerTrait(MooshroomVariant.class);
        registerTrait(FoxType.class);
        registerTrait(Age.class);
        registerTrait(BreedMode.class);
        registerTrait(LoveMode.class);
        registerTrait(DrownedItem.class);
        registerTrait(ZombieBaby.class);
        registerTrait(BeeNectar.class);
        registerTrait(BeeStung.class);
        registerTrait(Leash.class);
        registerTrait(Potion.class);
        registerTrait(VillagerProfession.class);
        if (Utilities.isPaper()) {
            registerTrait(TurtleHasEgg.class);
        }
        registerTrait(ZoglinBaby.class);
        registerTrait(PiglinBaby.class);
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_19_R1)) {
            registerTrait(FrogVariant.class);
        }
        registerTrait(AllayOwner.class);
    }

    /**
     * If a class hasn't been disabled in the config, add this to the hashset so it can be looped over.
     *
     * TODO: Perhaps there could be a hashset which contains a list of entity types that should be checked.
     * @param trait class that implements trait
     * @throws IllegalAccessException if class is not accessible
     * @throws InstantiationException if class can not be instantiated
     * @throws NoSuchMethodException if class constructor can not be found
     * @throws InvocationTargetException if instanciation fails
     */
    private <T extends Trait<? extends LivingEntity>> void registerTrait(Class<T> trait) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final TraitMetadata traitMetadata = trait.getAnnotation(TraitMetadata.class);
        if (sm.getMainConfig().getConfig().isTraitEnabled(traitMetadata.path()) || sm.getMainConfig().getConfig().isTraitEnabled(traitMetadata.path())) {
            traits.add((Trait<LivingEntity>) trait.getDeclaredConstructor().newInstance());
        }
    }

    /**
     * Check if the two given entities have any non-matching characteristics which prevent stacking.
     * @param first 1st entity to check
     * @param nearby entity to compare with
     * @return if these entities have any not matching characteristics (traits.)
     */
    public boolean checkTraits(StackEntity first, StackEntity nearby) {
        for (Trait<LivingEntity> trait : traits) {
            if (isTraitApplicable(trait, first.getEntity())) {
                if (trait.checkTrait(first.getEntity(), nearby.getEntity())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Apply the characteristics of the dead entity to the newly spawned entity.
     * @param spawned the entity that the traits should be copied to.
     * @param dead the entity which traits should be copied from.
     */
    public void applyTraits(StackEntity spawned, StackEntity dead) {
        for (Trait<LivingEntity> trait : traits) {
            if (isTraitApplicable(trait, spawned.getEntity())) {
                trait.applyTrait(spawned.getEntity(), dead.getEntity());
            }
        }
    }

    /**
     * Check if the trait is applicable to the given entity.
     * @param trait the trait to check.
     * @param entity the entity to check.
     * @return if the trait is applicable to the given entity.
     */
    private boolean isTraitApplicable(Trait<? extends LivingEntity> trait, LivingEntity entity) {
        ParameterizedType parameterizedType = (ParameterizedType) trait.getClass().getGenericInterfaces()[0];
        Class<?> typeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return typeArgument.isAssignableFrom(entity.getClass());
    }
}

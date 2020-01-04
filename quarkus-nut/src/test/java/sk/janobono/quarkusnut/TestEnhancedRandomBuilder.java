package sk.janobono.quarkusnut;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldPredicates;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.randomizers.EmailRandomizer;
import io.github.benas.randombeans.randomizers.FirstNameRandomizer;
import io.github.benas.randombeans.randomizers.collection.SetRandomizer;
import sk.janobono.quarkusnut.domain.RoleName;
import sk.janobono.quarkusnut.so.UserSO;

import java.util.Random;

public class TestEnhancedRandomBuilder {

    public static class RoleNameRandomizer implements Randomizer<String> {

        @Override
        public String getRandomValue() {
            return RoleName.values()[new Random().nextInt(RoleName.values().length)].name();
        }

    }

    public static EnhancedRandom build() {
        return EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .randomize(FieldPredicates.named("username").and(FieldPredicates.inClass(UserSO.class)), new FirstNameRandomizer())
                .randomize(FieldPredicates.named("email").and(FieldPredicates.inClass(UserSO.class)), new EmailRandomizer())
                .randomize(FieldPredicates.inClass(UserSO.class).and(FieldPredicates.named("roles")), new SetRandomizer<>(new RoleNameRandomizer()))
                .build();
    }
}

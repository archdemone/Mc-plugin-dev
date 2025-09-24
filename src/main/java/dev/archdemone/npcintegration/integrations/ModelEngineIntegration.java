package dev.archdemone.npcintegration.integrations;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;

/**
 * Integration class for ModelEngine plugin
 * Handles custom model creation and management through ModelEngine API
 */
public class ModelEngineIntegration {
    
    private final NPCIntegrationPlugin plugin;
    
    public ModelEngineIntegration(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Applies a custom model to an entity
     * @param entity The entity to apply the model to
     * @param modelName The name of the model to apply
     * @return true if successful, false otherwise
     */
    public boolean applyModel(Entity entity, String modelName) {
        try {
            // Get or create a modeled entity
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
            
            // Check if the model exists
            if (!ModelEngineAPI.isModelRegistered(modelName)) {
                plugin.getLogger().warning("Model not found: " + modelName);
                return false;
            }
            
            // Add the model to the entity
            modeledEntity.addActiveModel(modelName);
            
            plugin.getLogger().info("Applied model '" + modelName + "' to entity: " + entity.getType());
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to apply model: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Removes a custom model from an entity
     * @param entity The entity to remove the model from
     * @param modelName The name of the model to remove
     * @return true if successful, false otherwise
     */
    public boolean removeModel(Entity entity, String modelName) {
        try {
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
            modeledEntity.removeActiveModel(modelName);
            
            plugin.getLogger().info("Removed model '" + modelName + "' from entity: " + entity.getType());
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to remove model: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a model is applied to an entity
     * @param entity The entity to check
     * @param modelName The name of the model to check for
     * @return true if the model is applied
     */
    public boolean hasModel(Entity entity, String modelName) {
        try {
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
            return modeledEntity.getActiveModel(modelName).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the active model for an entity
     * @param entity The entity to check
     * @param modelName The name of the model
     * @return Optional containing the active model if found
     */
    public Optional<ActiveModel> getActiveModel(Entity entity, String modelName) {
        try {
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
            return modeledEntity.getActiveModel(modelName);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * Plays an animation on an entity's model
     * @param entity The entity with the model
     * @param modelName The name of the model
     * @param animationName The name of the animation to play
     * @return true if successful
     */
    public boolean playAnimation(Entity entity, String modelName, String animationName) {
        try {
            Optional<ActiveModel> activeModelOpt = getActiveModel(entity, modelName);
            if (activeModelOpt.isPresent()) {
                activeModelOpt.get().playAnimation(animationName);
                plugin.getLogger().info("Playing animation '" + animationName + "' on model '" + modelName + "'");
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to play animation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Stops an animation on an entity's model
     * @param entity The entity with the model
     * @param modelName The name of the model
     * @param animationName The name of the animation to stop
     * @return true if successful
     */
    public boolean stopAnimation(Entity entity, String modelName, String animationName) {
        try {
            Optional<ActiveModel> activeModelOpt = getActiveModel(entity, modelName);
            if (activeModelOpt.isPresent()) {
                activeModelOpt.get().stopAnimation(animationName);
                plugin.getLogger().info("Stopped animation '" + animationName + "' on model '" + modelName + "'");
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to stop animation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a model is registered
     * @param modelName The name of the model to check
     * @return true if the model is registered
     */
    public boolean isModelRegistered(String modelName) {
        return ModelEngineAPI.isModelRegistered(modelName);
    }
    
    /**
     * Gets a modeled entity wrapper
     * @param entity The entity to wrap
     * @return The modeled entity wrapper
     */
    public ModeledEntity getModeledEntity(Entity entity) {
        return ModelEngineAPI.getModeledEntity(entity);
    }
    
    /**
     * Checks if an entity has any models applied
     * @param entity The entity to check
     * @return true if the entity has models
     */
    public boolean isModeledEntity(Entity entity) {
        try {
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity);
            return !modeledEntity.getActiveModels().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if ModelEngine is available
     * @return true if ModelEngine is available
     */
    public boolean isAvailable() {
        try {
            return ModelEngineAPI.getModelManager() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
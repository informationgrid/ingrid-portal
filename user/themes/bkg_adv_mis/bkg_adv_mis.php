<?php
namespace Grav\Theme;

use Grav\Common\File\CompiledYamlFile;
use Grav\Common\Theme;
use Grav\Plugin\ElasticsearchHelper;
use RocketTheme\Toolbox\Event\Event;

class BkgAdvMis extends Theme
{

    public static function getSubscribedEvents(): array
    {
        return [
            'onThemeInitialized' => ['onThemeInitialized', 0],
            'onThemeSearchHitMetadataEvent' => ['addThemeSearchHitMetadataContent', 0],
        ];
    }

    public function onThemeInitialized()
    {
        if (!$this->isAdmin()) {
            // Load default configuration.
            $file = CompiledYamlFile::instance("themes://{$this->name}/config/override/override" . YAML_EXT);

            if ($file->exists()) {
                $themeOverrideConfig = $file->content();
                $this->config->set(
                    "themes.{$this->name}",
                    array_replace_recursive($this->config(), $themeOverrideConfig)
                );
            }
        }
    }

    public function addThemeSearchHitMetadataContent(Event $event): void
    {
        // Get variables from event
        $content = $event['content'];
        $hit = $event['hit'];
        $lang = $event['lang'];

        $hit->hierarchyLevel = ElasticsearchHelper::getValue($content, "hierarchylevel");

    }
}

(ns {{project-name}}.utils
  (:require {% for req in require %}
    {{req}}
    {% endfor %})
  (:import {% for imp in import %}
    {{imp}}
    {% endfor %}))

{% for fn in function %}
{{fn|safe}}
{% endfor%}
(ns {{project-name}}.utils
  (:require {% for require in requires %}
    {{require}}
    {% endfor %})
  (:import {% for import in imports %}
    {{import}}
    {% endfor %}))

{% for fn in fns %}
{{fn}}
{% endfor%}
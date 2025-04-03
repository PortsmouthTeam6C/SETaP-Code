import os
import sys
sys.path.insert(0, os.path.abspath('..'))  # Ensures ReadTheDocs can find your code

extensions = ['sphinx.ext.autodoc', 'sphinx.ext.napoleon']
extensions.append('myst_parser')
html_theme = 'sphinx_rtd_theme'

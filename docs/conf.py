import os
import sys
sys.path.insert(0, os.path.abspath('..'))  # Ensures ReadTheDocs can find your code

project = 'Synergy'
copyright ='2025, PortsmouthTeam6C'
author = 'PortsmouthTeam6C'

extensions = ['sphinx.ext.autodoc', 'sphinx.ext.napoleon']
extensions.append('myst_parser')
html_theme = 'sphinx_rtd_theme'

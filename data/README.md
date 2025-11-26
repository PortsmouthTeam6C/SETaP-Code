# Essay Questions Auto-Grading Dataset

This directory contains the Essay Questions Auto-Grading dataset from Hugging Face.

## Dataset Source

The dataset is sourced from: https://huggingface.co/datasets/mohamedemam/Essay-quetions-auto-grading

## How to Download the Dataset

### Option 1: Using Git LFS (Recommended)

```bash
# Navigate to this directory
cd data

# Clone the dataset repository
git clone https://huggingface.co/datasets/mohamedemam/Essay-quetions-auto-grading Essay-quetions-auto-grading
```

### Option 2: Using the Python datasets library

```bash
# Install the datasets library
pip install datasets

# Run Python script to download
python download_dataset.py
```

### Option 3: Manual Download

1. Visit https://huggingface.co/datasets/mohamedemam/Essay-quetions-auto-grading
2. Click on "Files and versions"
3. Download the required files and place them in this directory

## Dataset Structure

After downloading, the dataset should be located in:
```
data/
└── Essay-quetions-auto-grading/
    └── [dataset files]
```

## Usage

This dataset can be used for automated essay grading tasks.

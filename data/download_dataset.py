#!/usr/bin/env python3
"""
Script to download the Essay Questions Auto-Grading dataset from Hugging Face.

Usage:
    pip install datasets
    python download_dataset.py
"""

import os
from datasets import load_dataset

def main():
    # Get the directory where this script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, "Essay-quetions-auto-grading")
    
    print("Downloading Essay Questions Auto-Grading dataset from Hugging Face...")
    
    # Load the dataset from Hugging Face
    dataset = load_dataset("mohamedemam/Essay-quetions-auto-grading")
    
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)
    
    # Save the dataset locally
    dataset.save_to_disk(output_dir)
    
    print(f"Dataset successfully downloaded and saved to: {output_dir}")
    print(f"Dataset info: {dataset}")

if __name__ == "__main__":
    main()

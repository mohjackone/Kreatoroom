import filecmp
import logging
from os import path
from google.cloud import storage
from src.helper import load_config

dict_file_types = ["json", "yml", "yaml"]
logger = logging.getLogger(__name__)
project_path = path.dirname(path.dirname(path.abspath(__file__)))


def download_blob(bucket_name, source_blob_name, destination_file_name):
    """Downloads a blob from the bucket."""
    storage_client = storage.Client()
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(source_blob_name)

    blob.download_to_filename(destination_file_name)
    logger.info(f"Blob {source_blob_name} downloaded to {destination_file_name}.")


def reproducibility_tests(args):
    """Runs commands in config file and compares the generated files to those that are expected."""
    config_path = project_path + "/" + args.config

    modules = load_config(config_path)

    all_passed = True
    for module in modules:
        # log the path for test outcome and expected outcome of the module
        conf = modules[module]

        # download files from GCS if using GCP
        for file in conf["files_to_compare"]:
            true_gcs_path = conf["true_gcs_path"]
            test_gcs_path = conf["test_gcs_path"]
            download_blob(conf["bucket_name"], f"{true_gcs_path}/{file}", f"{conf['true_dir']}/{file}")
            download_blob(conf["bucket_name"], f"{test_gcs_path}/{file}", f"{conf['test_dir']}/{file}")

        # compare whether csv files generated by the model pipeline is the same as the expected files
        # located in test/true folder
        true_dir, test_dir = conf["true_dir"], conf["test_dir"]
        files_to_compare = [f for f in conf["files_to_compare"] if f.split('.')[-1] not in dict_file_types]
        match, mismatch, errors = filecmp.cmpfiles(true_dir, test_dir, files_to_compare, shallow=True)
        # if there is a mismatch or no file is match, reproducibility test is failed
        if len(mismatch) > 0 or len(match) == 0:
            logger.error(
                "{} file(s) do(es) not match, reproducibility test of model pipeline step {}': FAILED".format(mismatch,
                                                                                                              module))
            all_passed = False
        else:
            logger.info("Reproducibility test of model pipeline stage {}: PASSED".format(module))

    if all_passed:
        logger.info("Success, all reproducibility tests passed!")
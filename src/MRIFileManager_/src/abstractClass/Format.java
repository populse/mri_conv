package abstractClass;

public interface Format {

	public static final int Bruker = 0, Dicom = 1, Philips = 2, Nifti = 3, Bids = 4;

	public enum format {
		Bruker("Bruker"), Dicom("Dicom"), Philips("Philips"), Nifti("Nifti"), Bids("Bids");

		private int nb;

		format(String name) {
			if (name.contains("Bruker"))
				nb = 0;
			if (name.contains("Dicom"))
				nb = 1;
			if (name.contains("Philips"))
				nb = 2;
			if (name.contains("Nifti"))
				nb = 3;
			if (name.contains("Bids"))
				nb = 4;
		}

		public int toInt() {
			return nb;
		}
	}

	public enum NiftiHeadertoJson {
		ANZ_HDR_SIZE, NII_HDR_SIZE, NII_MAGIC_STRING, big_endian, XDIM, YDIM, ZDIM, TDIM, DIM5, DIM6, DIM7, freq_dim,
		phase_dim, slice_dim, xyz_unit_code, t_unit_code, qfac, sizeof_hdr, data_type_string, db_name, extents,
		session_error, regular, dim_info, intent, intent_code, slice_start, pixdim, vox_offset, scl_slope, scl_inter,
		slice_end, slice_code, xyzt_units, cal_max, cal_min, slice_duration, toffset, glmax, glmin, descrip, aux_file,
		qform_code, sform_code, quatern, qoffset, srow_x, srow_y, srow_z, intent_name, magic, extension
	}
}
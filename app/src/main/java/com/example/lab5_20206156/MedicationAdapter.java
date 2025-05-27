package com.example.lab5_20206156;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MedicationAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication currentMedication = medicationList.get(position);

        holder.textViewMedicationName.setText(currentMedication.getName());
        holder.textViewMedicationTypeDosage.setText(currentMedication.getDosage() + " " + currentMedication.getType());
        holder.textViewMedicationFrequency.setText("Cada " + currentMedication.getFrequencyHours() + " horas");

        // Formatear la fecha y hora de inicio
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(currentMedication.getStartDateMillis());
        holder.textViewMedicationStartDate.setText("Desde: " + formattedDate);

        holder.imageViewDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMedicationName;
        TextView textViewMedicationTypeDosage;
        TextView textViewMedicationFrequency;
        TextView textViewMedicationStartDate;
        ImageView imageViewDelete;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedicationName = itemView.findViewById(R.id.textViewMedicationName);
            textViewMedicationTypeDosage = itemView.findViewById(R.id.textViewMedicationTypeDosage);
            textViewMedicationFrequency = itemView.findViewById(R.id.textViewMedicationFrequency);
            textViewMedicationStartDate = itemView.findViewById(R.id.textViewMedicationStartDate);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }

    // Método para actualizar la lista de medicamentos
    public void updateMedications(List<Medication> newMedications) {
        this.medicationList = newMedications;
        notifyDataSetChanged();
    }
}
